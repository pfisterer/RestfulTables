/**
 * Copyright (c) 2012, Dennis Pfisterer, Institute of Telematics, University of Luebeck
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 	- Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * 	  disclaimer.
 * 	- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * 	  following disclaimer in the documentation and/or other materials provided with the distribution.
 * 	- Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or promote
 * 	  products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.farberg.restfultables;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.LoggerFactory;

import de.farberg.restfultables.iptables.IptablesCommand;
import de.farberg.restfultables.iptables.IptablesInterface;
import de.farberg.restfultables.iptables.IptablesSerializer;
import de.farberg.restfultables.xml.IptablesOptions;

@Path("/restfultables")
public class Service {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Service.class);

    @Inject
    private IptablesInterface iptables;
    
    @Inject
    private IptablesSerializer serializer;

    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    public Response help() throws IOException {
        log.debug("Sending help");
        List<String> result = serializer.convert(null, IptablesCommand.help, null);
        return Response.ok(iptables.execute(result).getSecond()).build();
    }

    /**
     * Commands: list, list-rules
     * 
     * @return
     * @throws IOException 
     */
    @GET
    @Path("tables/{table}/")
    @Produces({ MediaType.TEXT_PLAIN })
    public Response listAllRulesOfAllChains(@PathParam("table") final String table) throws IOException {
        log.debug("Listing rules in table {}", table);

        List<String>  result = serializer.convert(table, IptablesCommand.list, null);
        return Response.ok(iptables.execute(result).getSecond()).build();
    }

    /**
     * Tables: filter, nat, mangle
     * 
     * @return
     * @throws IOException 
     */
    @GET
    @Path("tables/{table}/{chain}")
    public Response listChain(@PathParam("table") final String table, @PathParam("chain") final String chain) throws IOException {
        log.debug("Listing rules in table {} and chain {}", table, chain);

        IptablesOptions opts = new IptablesOptions();
        opts.setChain(chain);
        
        List<String>  result = serializer.convert(table, IptablesCommand.listrules, opts);
        return Response.ok(iptables.execute(result).getSecond()).build();        
    }

    /**
     * Tables: filter, nat, mangle
     * 
     * Commands: append, delete, insert, replace, flush, zero, new-chain, policy, rename-chain
     * @throws IOException 
     */
    @POST
    @Path("tables/{table}/{command}")
    @Consumes({ MediaType.TEXT_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_JSON })
    public Response executeCommand(IptablesOptions options, @PathParam("table") final String table, @PathParam("command") final String command) throws IOException {
        log.debug("Received command {}", options);

        List<String>  result = serializer.convert(table, IptablesCommand.fromIptablesCommandString(command), options);
        return Response.ok(iptables.execute(result).getSecond()).build();        
    }

}
