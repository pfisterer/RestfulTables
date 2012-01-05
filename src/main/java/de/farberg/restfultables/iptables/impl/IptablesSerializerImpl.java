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

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Preconditions;

import de.farberg.restfultables.iptables.IptablesCommand;
import de.farberg.restfultables.iptables.IptablesSerializer;
import de.farberg.restfultables.xml.IptablesOptions;
import de.farberg.restfultables.xml.NegateableString;
import de.farberg.restfultables.xml.RuleSpecification;
import de.farberg.restfultables.xml.RuleSpecification.TcpOptions;
import de.farberg.restfultables.xml.RuleSpecification.UdpOptions;

public class IptablesSerializerImpl implements IptablesSerializer {

    @Override
    public List<String> convert(String table, IptablesCommand command, IptablesOptions options) {
        List<String> b = new LinkedList<String>();

        addIfNonNull(b, "--table ", table);

        addIfNonNull(b, "--" + command.toIpTablesCommandString());

        if (options == null)
            return b;

        boolean addRuleSpec = false;

        if (command == IptablesCommand.append) {

            Preconditions.checkNotNull(options.getChain(), "Chain must not be null");
            addIfNonNull(b, options.getChain());
            addRuleSpec = true;

        } else if (command == IptablesCommand.delete) {

            Preconditions.checkNotNull(options.getChain(), "Chain must not be null");

            if (options.getRuleNumber() != null) {
                addIfNonNull(b, " ", options.getChain());
            } else {
                addRuleSpec = true;
            }

        } else if (command == IptablesCommand.insert) {

            Preconditions.checkNotNull(options.getChain(), "Chain must not be null");
            addIfNonNull(b, options.getChain());
            addIfNonNull(b, options.getRuleNumber());
            addRuleSpec = true;

        } else if (command == IptablesCommand.replace) {

            Preconditions.checkNotNull(options.getChain(), "Chain must not be null");
            Preconditions.checkNotNull(options.getRuleNumber(), "RuleNumber must not be null");
            addIfNonNull(b, options.getChain());
            addIfNonNull(b, options.getRuleNumber());
            addRuleSpec = true;

        } else if (command == IptablesCommand.list || command == IptablesCommand.listrules
                || command == IptablesCommand.flush) {

            addIfNonNull(b, options.getChain());

        } else if (command == IptablesCommand.zero) {

            addIfNonNull(b, options.getChain());
            if (options.getChain() != null)
                addIfNonNull(b, options.getRuleNumber());

        } else if (command == IptablesCommand.newchain) {

            Preconditions.checkNotNull(options.getChain(), "Chain must not be null");
            addIfNonNull(b, options.getChain());

        } else if (command == IptablesCommand.deletechain) {

            addIfNonNull(b, options.getChain());

        } else if (command == IptablesCommand.policy) {

            Preconditions.checkNotNull(options.getChain(), "Chain must not be null");
            Preconditions.checkNotNull(options.getTarget(), "Target must not be null");
            addIfNonNull(b, options.getChain());
            addIfNonNull(b, options.getTarget());

        } else if (command == IptablesCommand.renamechain) {

            Preconditions.checkNotNull(options.getChain(), "Chain must not be null");
            Preconditions.checkNotNull(options.getNewChain(), "NewChain must not be null");
            addIfNonNull(b, options.getChain());
            addIfNonNull(b, options.getNewChain());

        }

        if (addRuleSpec) {
            RuleSpecification rs = options.getRuleSpecification();

            addIfNonNull(b, "--protocol ", rs.getProtocol());
            addIfNonNull(b, "--source ", rs.getSource());
            addIfNonNull(b, "--dest ", rs.getDestination());
            addIfNonNull(b, "--in-interface ", rs.getInInterface());
            addIfNonNull(b, "--out-interface ", rs.getOutInterface());

            addIfPresent(b, "--fragment", rs.getFragment());

            if (rs.getSetCounters() != null) {
                b.add("--set-counters ");
                b.add("" + rs.getSetCounters().getPackets());
                b.add("" + rs.getSetCounters().getBytes());
            }

            if ("tcp".equals(rs.getProtocol())) {
                appendTcpOptions(b, rs.getTcpOptions());
            } else if ("udp".equals(rs.getProtocol())) {
                appendUdpOptions(b, rs.getUdpOptions());
            }

            if (rs.getJump() != null)
                addIfNonNull(b, "--jump ", rs.getJump());
            else
                addIfNonNull(b, "--goto ", rs.getGoto());
        }

        return b;
    }

    private void appendTcpOptions(List<String> builder, TcpOptions options) {
        addIfNonNull(builder, "--source-port ", options.getSourcePort());
        addIfNonNull(builder, "--destination-port ", options.getDestinationPort());
        // TODO: Missing: TCP flags
        addIfPresent(builder, "--syn", options.getSyn());
        addIfNonNull(builder, "--tcp-option ", options.getTcpOption());
    }

    private void appendUdpOptions(List<String> builder, UdpOptions options) {
        addIfNonNull(builder, "--source-port ", options.getSourcePort());
        addIfNonNull(builder, "--destination-port ", options.getDestinationPort());
    }

    private void addIfPresent(List<String> builder, String command, Boolean flag) {
        if (flag == null)
            return;

        if (flag.booleanValue() == false)
            builder.add(" ! ");
        builder.add(command);
    }

    private void addIfNonNull(List<String> builder, String prefix, NegateableString ns) {
        if (ns != null && ns.getValue() != null) {
            if (ns.getNegate() == true)
                builder.add(" ! ");
            addIfNonNull(builder, prefix, ns.getValue());
        }
    }

    private void addIfNonNull(List<String> builder, Integer integer) {
        if (integer != null)
            addIfNonNull(builder, null, integer.toString());
    }

    private void addIfNonNull(List<String> builder, String string) {
        addIfNonNull(builder, null, string);
    }

    private void addIfNonNull(List<String> builder, String prefix, String string) {
        if (string != null) {
            if (prefix != null)
                builder.add(prefix);
            builder.add(string);
        }
    }

}
