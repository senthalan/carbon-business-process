/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.bpel.analytics.publisher.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.bpel.analytics.publisher.Axis2ConfigurationContextObserverImpl;
import org.wso2.carbon.bpel.core.BPELEngineService;
import org.wso2.carbon.bpel.core.ode.integration.BPELServer;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.utils.Axis2ConfigurationContextObserver;

@Component(
        name = "org.wso2.carbon.bpel.AnalyticsPublisherServiceComponent",
        immediate = true)
public class AnalyticsPublisherServiceComponent {

    private static Log log = LogFactory.getLog(AnalyticsPublisherServiceComponent.class);

    private BundleContext bundleContext;

    @Activate
    protected void activate(ComponentContext ctx) {

        this.bundleContext = ctx.getBundleContext();
        if (log.isDebugEnabled()) {
            log.debug("BPEL Analytics publisher bundle is activated.");
        }
        registerAxis2ConfigurationContextObserver();
    }

    @Reference(
            name = "bpel.engine",
            service = org.wso2.carbon.bpel.core.BPELEngineService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetBPELServer")
    protected void setBPELServer(BPELEngineService bpelEngineService) {

        if (log.isDebugEnabled()) {
            log.debug("BPELEngineService bound to BEPL Analytics Publisher component");
        }
        BPELAnalyticsPublisherContentHolder.getInstance().setBpelServer(bpelEngineService.getBPELServer());
    }

    protected void unsetBPELServer(BPELEngineService bpelEngineService) {

        if (log.isDebugEnabled()) {
            log.debug("BPELEngineService unbound from the BPEL Analytics Publisher component");
        }
        BPELAnalyticsPublisherContentHolder.getInstance().setBpelServer(null);
    }

    @Reference(
            name = "registry.service",
            service = org.wso2.carbon.registry.core.service.RegistryService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRegistryService")
    protected void setRegistryService(RegistryService registryService) {

        if (log.isDebugEnabled()) {
            log.debug("Registry service bound to BPEL Analytics publisher component ");
        }
        BPELAnalyticsPublisherContentHolder.getInstance().setRegistryService(registryService);
    }

    private void registerAxis2ConfigurationContextObserver() {

        this.bundleContext.registerService(Axis2ConfigurationContextObserver.class.getName(), new
                Axis2ConfigurationContextObserverImpl(), null);
    }

    protected void unsetRegistryService(RegistryService registrySvc) {

        if (log.isDebugEnabled()) {
            log.debug("RegistryService unbound from the BPEL component");
        }
        BPELAnalyticsPublisherContentHolder.getInstance().setRegistryService(null);
    }

    public static BPELServer getBPELServer() {

        return BPELAnalyticsPublisherContentHolder.getInstance().getBpelServer();
    }

    public static RegistryService getRegistryService() {

        return BPELAnalyticsPublisherContentHolder.getInstance().getRegistryService();
    }

    @Deactivate
    protected void deactivate(ComponentContext componentContext) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("Stopping the BPEL Analytics publisher component");
        }
    }
}
