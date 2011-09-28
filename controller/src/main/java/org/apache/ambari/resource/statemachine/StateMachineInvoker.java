/**
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.ambari.resource.statemachine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.ambari.components.ClusterContext;
import org.apache.ambari.event.AsyncDispatcher;
import org.apache.ambari.event.Dispatcher;
import org.apache.ambari.event.EventHandler;

public class StateMachineInvoker {
  
  private static Dispatcher dispatcher;
  
  static {
    dispatcher = new AsyncDispatcher();
    dispatcher.register(ClusterEventType.class, new ClusterEventDispatcher());
    dispatcher.register(ServiceEventType.class, new ServiceEventDispatcher());
  }

  public Dispatcher getAMBARIDispatcher() {
    return dispatcher;
  }

  public static EventHandler getAMBARIEventHandler() {
    return dispatcher.getEventHandler();
  }

  public static class ClusterEventDispatcher 
  implements EventHandler<ClusterEvent> {
    @Override
    public void handle(ClusterEvent event) {
      ((EventHandler<ClusterEvent>)event.getCluster()).handle(event);
    }
  }
  
  public static class ServiceEventDispatcher 
  implements EventHandler<ServiceEvent> {
    @Override
    public void handle(ServiceEvent event) {
      ((EventHandler<ServiceEvent>)event.getService()).handle(event);
    }
  }
  
  private static ConcurrentMap<String, Cluster> clusters = 
      new ConcurrentHashMap<String, Cluster>();
  public static Cluster createCluster(String clusterId) {
    ClusterImpl cluster = new ClusterImpl(clusterId);
    clusters.put(clusterId, cluster);
    return cluster;
  }
  
  public static Cluster getStateMachineClusterInstance(String clusterId) {
    return clusters.get(clusterId);
  }
  
  public static Service addServiceInCluster(Cluster cluster, String serviceName) 
      throws IOException {
    Service service = new ServiceImpl(cluster, serviceName);
    return addServiceInCluster(cluster, service);
  }
  
  public static Service addServiceInCluster(Cluster cluster, Service service) {
    List<Service> serviceList = new ArrayList<Service>();
    serviceList.add(service);
    cluster.addServices(serviceList);
    return service;
  }
    
}
