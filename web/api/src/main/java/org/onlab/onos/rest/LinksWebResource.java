/*
 * Copyright 2014 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onlab.onos.rest;

import org.onlab.onos.net.ConnectPoint;
import org.onlab.onos.net.DeviceId;
import org.onlab.onos.net.Link;
import org.onlab.onos.net.link.LinkService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static org.onlab.onos.net.DeviceId.deviceId;
import static org.onlab.onos.net.PortNumber.portNumber;
import static org.onlab.onos.rest.LinksWebResource.Direction.valueOf;

/**
 * REST resource for interacting with the inventory of infrastructure links.
 */
@Path("links")
public class LinksWebResource extends AbstractWebResource {

    enum Direction { ALL, INGRESS, EGRESS }

    @GET
    public Response getLinks(@QueryParam("device") String deviceId,
                             @QueryParam("port") String port,
                             @QueryParam("direction") String direction) {
        LinkService service = get(LinkService.class);
        Iterable<Link> links;

        if (deviceId != null && port != null) {
            links = getConnectPointLinks(new ConnectPoint(deviceId(deviceId),
                                                          portNumber(port)),
                                         direction, service);
        } else if (deviceId != null) {
            links = getDeviceLinks(deviceId(deviceId), direction, service);
        } else {
            links = service.getLinks();
        }
        return ok(encodeArray(Link.class, "links", links)).build();
    }

    private Iterable<Link> getConnectPointLinks(ConnectPoint point,
                                                String direction,
                                                LinkService service) {
        Direction dir = direction != null ?
                valueOf(direction.toUpperCase()) : Direction.ALL;
        switch (dir) {
            case INGRESS:
                return service.getIngressLinks(point);
            case EGRESS:
                return service.getEgressLinks(point);
            default:
                return service.getLinks(point);
        }
    }

    private Iterable<Link> getDeviceLinks(DeviceId deviceId,
                                          String direction,
                                          LinkService service) {
        Direction dir = direction != null ?
                valueOf(direction.toUpperCase()) : Direction.ALL;
        switch (dir) {
            case INGRESS:
                return service.getDeviceIngressLinks(deviceId);
            case EGRESS:
                return service.getDeviceEgressLinks(deviceId);
            default:
                return service.getDeviceLinks(deviceId);
        }
    }

}
