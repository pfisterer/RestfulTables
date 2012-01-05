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
package de.farberg.restfultables.iptables.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.common.io.ByteStreams;

import de.farberg.restfultables.iptables.IptablesInterface;
import de.uniluebeck.itm.tr.util.Tuple;

public class IptablesExecutor implements IptablesInterface {
    private String iptablesCommand;

    public IptablesExecutor() {
        this("iptables");
    }

    public IptablesExecutor(String iptablesCommand) {
        this.iptablesCommand = iptablesCommand;
    }

    @Override
    public Tuple<Integer, String> execute(List<String> options) throws IOException {
        List<String> cmd = new LinkedList<>();
        cmd.add(iptablesCommand);
        cmd.addAll(options);
        ProcessBuilder builder = new ProcessBuilder(cmd);
        Process process = builder.start();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        ByteStreams.copy(process.getInputStream(), outputStream);
        
        return new Tuple<Integer, String>(process.exitValue(), outputStream.toString());
    }

}
