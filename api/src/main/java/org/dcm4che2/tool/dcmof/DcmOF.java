/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at http://sourceforge.net/projects/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Gunter Zeilinger, Huetteldorferstr. 24/10, 1150 Vienna/Austria/Europe.
 * Portions created by the Initial Developer are Copyright (C) 2002-2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Gunter Zeilinger <gunterze@gmail.com>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4che2.tool.dcmof;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.UID;
import org.dcm4che2.io.ContentHandlerAdapter;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.io.SAXWriter;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.service.VerificationService;
import org.dcm4che2.util.CloseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Revision: 11855 $ $Date: 2009-06-23 13:11:51 +0200 (Tue, 23 Jun 2009) $
 * @since Jan 22, 2006
 * 
 */
public class DcmOF {
    static Logger LOG = LoggerFactory.getLogger(DcmOF.class);

    private static final int KB = 1024;

    private static final String USAGE = "dcmof [Options] [<aet>[@<ip>]:]<port>";

    private static final String DESCRIPTION = "DICOM Server providing DICOM service of IHE actor Order Filler:\n"
            + "- Modality Worklist (MWL SCP),\n"
            + "- Modality Performed Procedure Step (MPPS SCP)\n"
            + "- Image Availability Notification (IAN SCP)\n"
            + "- Basic Study Content Notification (SCN SCP) {not specified by IHE}\n"
            + "listening on specified <port> for incoming association requests. "
            + "If no local IP address of the network interface is specified "
            + "connections on  any/all local addresses are accepted. "
            + "If <aet> is specified, only requests with matching called AE "
            + "title will be accepted.\n" + "Options:";

    private static final String EXAMPLE = "\nExample 1: dcmof DCM4CHE_OF:11112 -mwl /var/local/dcmof/mwl\n"
            + "=> Starts MWL SCP listening on port 11112, accepting association "
            + "requests with DCM4CHE_OF as called AE title, provides worklist items "
            + "stored in files in directory /var/local/dcmof/mwl as MWL SCP.\n"
            + "Example 2: dcmof DCM4CHE_OF:11112 -mpps /tmp -ian /tmp -scn /tmp\n"
            + "=> Starts MPPS+IAN+SCN SCP listening on port 11112, accepting association "
            + "requests with DCM4CHE_OF as called AE title, storing received messages "
            + "to /tmp.";

    private static String[] TLS1 = { "TLSv1" };

    private static String[] SSL3 = { "SSLv3" };

    private static String[] NO_TLS1 = { "SSLv3", "SSLv2Hello" };

    private static String[] NO_SSL2 = { "TLSv1", "SSLv3" };

    private static String[] NO_SSL3 = { "TLSv1", "SSLv2Hello" };

    private static char[] SECRET = { 's', 'e', 'c', 'r', 'e', 't' };
    
    private static final String[] ONLY_DEF_TS = { UID.ImplicitVRLittleEndian };

    private static final String[] NATIVE_TS = { UID.ExplicitVRLittleEndian,
            UID.ExplicitVRBigEndian, UID.ImplicitVRLittleEndian };

    private static final String[] NATIVE_LE_TS = { UID.ExplicitVRLittleEndian,
            UID.ImplicitVRLittleEndian };

    private final Executor executor;

    private final Device device;

    private final NetworkApplicationEntity ae = new NetworkApplicationEntity();

    private final NetworkConnection nc = new NetworkConnection();

    private String[] tsuids = NATIVE_LE_TS;

    private boolean indent = false;
    private boolean comments = false;

    private String keyStoreURL = "resource:tls/test_sys_2.p12";
    
    private char[] keyStorePassword = SECRET; 

    private char[] keyPassword; 
    
    private String trustStoreURL = "resource:tls/mesa_certs.jks";
    
    private char[] trustStorePassword = SECRET; 
    
    public DcmOF(String name) {
        device = new Device(name);
        executor = new NewThreadExecutor(name);
        device.setNetworkApplicationEntity(ae);
        device.setNetworkConnection(nc);
        ae.setNetworkConnection(nc);
        ae.setAssociationAcceptor(true);
        ae.register(new VerificationService());
    }

    public final void setAEtitle(String aet) {
        ae.setAETitle(aet);
    }

    public final void setHostname(String hostname) {
        nc.setHostname(hostname);
    }

    public final void setPort(int port) {
        nc.setPort(port);
    }

    public final void setTlsProtocol(String[] tlsProtocol) {
        nc.setTlsProtocol(tlsProtocol);
    }

    public final void setTlsWithoutEncyrption() {
        nc.setTlsWithoutEncyrption();
    }

    public final void setTls3DES_EDE_CBC() {
        nc.setTls3DES_EDE_CBC();
    }

    public final void setTlsAES_128_CBC() {
        nc.setTlsAES_128_CBC();
    }
    
    public final void setTlsNeedClientAuth(boolean needClientAuth) {
        nc.setTlsNeedClientAuth(needClientAuth);
    }
    
    public final void setKeyStoreURL(String url) {
        keyStoreURL = url;
    }
    
    public final void setKeyStorePassword(String pw) {
        keyStorePassword = pw.toCharArray();
    }
    
    public final void setKeyPassword(String pw) {
        keyPassword = pw.toCharArray();
    }
    
    public final void setTrustStorePassword(String pw) {
        trustStorePassword = pw.toCharArray();
    }
    
    public final void setTrustStoreURL(String url) {
        trustStoreURL = url;
    }

    public final void setPackPDV(boolean packPDV) {
        ae.setPackPDV(packPDV);
    }

    public final void setAssociationReaperPeriod(int period) {
        device.setAssociationReaperPeriod(period);
    }

    public final void setTcpNoDelay(boolean tcpNoDelay) {
        nc.setTcpNoDelay(tcpNoDelay);
    }

    public final void setRequestTimeout(int timeout) {
        nc.setRequestTimeout(timeout);
    }

    public final void setReleaseTimeout(int timeout) {
        nc.setReleaseTimeout(timeout);
    }

    public final void setSocketCloseDelay(int delay) {
        nc.setSocketCloseDelay(delay);
    }

    public final void setIdleTimeout(int timeout) {
        ae.setIdleTimeout(timeout);
    }

    public final void setDimseRspTimeout(int timeout) {
        ae.setDimseRspTimeout(timeout);
    }

    public final void setMaxPDULengthSend(int maxLength) {
        ae.setMaxPDULengthSend(maxLength);
    }

    public void setMaxPDULengthReceive(int maxLength) {
        ae.setMaxPDULengthReceive(maxLength);
    }

    public final void setReceiveBufferSize(int bufferSize) {
        nc.setReceiveBufferSize(bufferSize);
    }

    public final void setSendBufferSize(int bufferSize) {
        nc.setSendBufferSize(bufferSize);
    }

    private static CommandLine parse(String[] args) {
        Options opts = new Options();
        
        OptionBuilder.withArgName("name");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(
                "set device name, use DCMOF by default");
        opts.addOption(OptionBuilder.create("device"));

        OptionBuilder.withArgName("NULL|3DES|AES");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(
                "enable TLS connection without, 3DES or AES encryption");
        opts.addOption(OptionBuilder.create("tls"));
        
        OptionGroup tlsProtocol = new OptionGroup();
        tlsProtocol.addOption(new Option("tls1",
                "disable the use of SSLv3 and SSLv2 for TLS connections"));
        tlsProtocol.addOption(new Option("ssl3",
                "disable the use of TLSv1 and SSLv2 for TLS connections"));
        tlsProtocol.addOption(new Option("no_tls1",
                "disable the use of TLSv1 for TLS connections"));
        tlsProtocol.addOption(new Option("no_ssl3",
                "disable the use of SSLv3 for TLS connections"));
        tlsProtocol.addOption(new Option("no_ssl2",
                "disable the use of SSLv2 for TLS connections"));
        opts.addOptionGroup(tlsProtocol);

        opts.addOption("noclientauth", false,
                "disable client authentification for TLS");        

        OptionBuilder.withArgName("file|url");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(
                "file path or URL of P12 or JKS keystore, resource:tls/test_sys_2.p12 by default");
        opts.addOption(OptionBuilder.create("keystore"));

        OptionBuilder.withArgName("password");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(
                "password for keystore file, 'secret' by default");
        opts.addOption(OptionBuilder.create("keystorepw"));

        OptionBuilder.withArgName("password");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(
                "password for accessing the key in the keystore, keystore password by default");
        opts.addOption(OptionBuilder.create("keypw"));

        OptionBuilder.withArgName("file|url");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(
                "file path or URL of JKS truststore, resource:tls/mesa_certs.jks by default");
        opts.addOption(OptionBuilder.create("truststore"));

        OptionBuilder.withArgName("password");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(
                "password for truststore file, 'secret' by default");
        opts.addOption(OptionBuilder.create("truststorepw"));
        
        OptionBuilder.withArgName("dir");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("Activate MWL SCP, providing MWL Items stored in specified directory.");
        opts.addOption(OptionBuilder.create("mwl"));

        OptionGroup mpps = new OptionGroup();
        OptionBuilder.withArgName("dir");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("Activate MPPS SCP, storing received MPPS in specified directory.");
        mpps.addOption(OptionBuilder.create("mpps"));
        OptionBuilder.withArgName("dir");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("Activate MPPS SCP, storing XML received MPPS in specified directory in XML format.");
        mpps.addOption(OptionBuilder.create("mppsxml"));
        opts.addOptionGroup(mpps);

        OptionGroup ian = new OptionGroup();
        OptionBuilder.withArgName("dir");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("Activate IAN SCP, storing received IAN in specified directory.");
        ian.addOption(OptionBuilder.create("ian"));
        OptionBuilder.withArgName("dir");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("Activate IAN SCP, storing received IAN in specified directory in XML format.");
        ian.addOption(OptionBuilder.create("ianxml"));
        opts.addOptionGroup(ian);

        OptionGroup scn = new OptionGroup();
        OptionBuilder.withArgName("dir");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("Activate SCN SCP, storing received SCN in specified directory.");
        scn.addOption(OptionBuilder.create("scn"));
        OptionBuilder.withArgName("dir");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("Activate SCN SCP, storing received SCN in specified directory in XML format.");
        scn.addOption(OptionBuilder.create("scnxml"));
        opts.addOptionGroup(scn);

        opts.addOption("c", "compact", false,
                "suppress additional whitespaces in XML output");
        opts.addOption("C", "comments", false,
                "include attribute names as comments in XML output");

        OptionGroup ts = new OptionGroup();
        OptionBuilder.withDescription("accept only default Transfer Syntax.");
        ts.addOption(OptionBuilder.create("defts"));
        OptionBuilder
                .withDescription("accept Explict VR Big Endian Transfer Syntax.");
        ts.addOption(OptionBuilder.create("bigendian"));
        opts.addOptionGroup(ts);

        OptionBuilder.withArgName("maxops");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("maximum number of outstanding operations performed "
                        + "asynchronously, unlimited by default.");
        opts.addOption(OptionBuilder.create("async"));

        opts.addOption("pdv1", false,
                "send only one PDV in one P-Data-TF PDU, " +
                "pack command and data PDV in one P-DATA-TF PDU by default.");
        opts.addOption("tcpdelay", false,
                "set TCP_NODELAY socket option to false, true by default");

        OptionBuilder.withArgName("ms");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("delay in ms for Socket close after sending A-ABORT, 50ms by default");
        opts.addOption(OptionBuilder.create("soclosedelay"));

        OptionBuilder.withArgName("ms");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("timeout in ms for receiving -ASSOCIATE-RQ, 5s by default");
        opts.addOption(OptionBuilder.create("requestTO"));

        OptionBuilder.withArgName("ms");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("timeout in ms for receiving A-RELEASE-RP, 5s by default");
        opts.addOption(OptionBuilder.create("releaseTO"));

        OptionBuilder.withArgName("ms");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("period in ms to check for outstanding DIMSE-RSP, 10s by default");
        opts.addOption(OptionBuilder.create("reaper"));

        OptionBuilder.withArgName("ms");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("timeout in ms for receiving DIMSE-RQ, 60s by default");
        opts.addOption(OptionBuilder.create("idleTO"));

        OptionBuilder.withArgName("KB");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("maximal length in KB of received P-DATA-TF PDUs, 16KB by default");
        opts.addOption(OptionBuilder.create("rcvpdulen"));

        OptionBuilder.withArgName("KB");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("maximal length in KB of sent P-DATA-TF PDUs, 16KB by default");
        opts.addOption(OptionBuilder.create("sndpdulen"));

        OptionBuilder.withArgName("KB");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("set SO_RCVBUF socket option to specified value in KB");
        opts.addOption(OptionBuilder.create("sorcvbuf"));

        OptionBuilder.withArgName("KB");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("set SO_SNDBUF socket option to specified value in KB");
        opts.addOption(OptionBuilder.create("sosndbuf"));

        opts.addOption("h", "help", false, "print this message");
        opts.addOption("V", "version", false,
                "print the version information and exit");

        CommandLine cl = null;
        try {
            cl = new GnuParser().parse(opts, args);
        } catch (ParseException e) {
            exit("dcmof: " + e.getMessage());
            throw new RuntimeException("unreachable");
        }
        if (cl.hasOption("V")) {
            Package p = DcmOF.class.getPackage();
            System.out.println("dcmof v" + p.getImplementationVersion());
            System.exit(0);
        }
        if (cl.hasOption("h") || cl.getArgList().size() == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(USAGE, DESCRIPTION, opts, EXAMPLE);
            System.exit(0);
        }
        return cl;
    }

    @SuppressWarnings("unchecked")
    public static DcmOF main(String[] args) {
        CommandLine cl = parse(args);
        DcmOF dcmof = new DcmOF(cl.hasOption("device") 
                ? cl.getOptionValue("device") : "DCMOF");
        final List<String> argList = cl.getArgList();
        String port = argList.get(0);
        String[] aetPort = split(port, ':', 1);
        dcmof.setPort(parseInt(aetPort[1], "illegal port number", 1, 0xffff));
        if (aetPort[0] != null) {
            String[] aetHost = split(aetPort[0], '@', 0);
            dcmof.setAEtitle(aetHost[0]);
            if (aetHost[1] != null) {
                dcmof.setHostname(aetHost[1]);
            }
        }

        if (cl.hasOption("defts"))
            dcmof.setTransferSyntax(ONLY_DEF_TS);
        else if (cl.hasOption("bigendian"))
            dcmof.setTransferSyntax(NATIVE_TS);
        if (cl.hasOption("reaper"))
            dcmof
                    .setAssociationReaperPeriod(parseInt(cl
                            .getOptionValue("reaper"),
                            "illegal argument of option -reaper", 1,
                            Integer.MAX_VALUE));
        if (cl.hasOption("idleTO"))
            dcmof
                    .setIdleTimeout(parseInt(cl.getOptionValue("idleTO"),
                            "illegal argument of option -idleTO", 1,
                            Integer.MAX_VALUE));
        if (cl.hasOption("requestTO"))
            dcmof.setRequestTimeout(parseInt(cl.getOptionValue("requestTO"),
                    "illegal argument of option -requestTO", 1,
                    Integer.MAX_VALUE));
        if (cl.hasOption("releaseTO"))
            dcmof.setReleaseTimeout(parseInt(cl.getOptionValue("releaseTO"),
                    "illegal argument of option -releaseTO", 1,
                    Integer.MAX_VALUE));
        if (cl.hasOption("soclosedelay"))
            dcmof.setSocketCloseDelay(parseInt(
                    cl.getOptionValue("soclosedelay"),
                    "illegal argument of option -soclosedelay", 1, 10000));
        if (cl.hasOption("rcvpdulen"))
            dcmof.setMaxPDULengthReceive(parseInt(
                    cl.getOptionValue("rcvpdulen"),
                    "illegal argument of option -rcvpdulen", 1, 10000)
                    * KB);
        if (cl.hasOption("sndpdulen"))
            dcmof.setMaxPDULengthSend(parseInt(cl.getOptionValue("sndpdulen"),
                    "illegal argument of option -sndpdulen", 1, 10000)
                    * KB);
        if (cl.hasOption("sosndbuf"))
            dcmof.setSendBufferSize(parseInt(cl.getOptionValue("sosndbuf"),
                    "illegal argument of option -sosndbuf", 1, 10000)
                    * KB);
        if (cl.hasOption("sorcvbuf"))
            dcmof.setReceiveBufferSize(parseInt(cl.getOptionValue("sorcvbuf"),
                    "illegal argument of option -sorcvbuf", 1, 10000)
                    * KB);

        dcmof.setPackPDV(!cl.hasOption("pdv1"));
        dcmof.setTcpNoDelay(!cl.hasOption("tcpdelay"));
        if (cl.hasOption("async"))
            dcmof.setMaxOpsPerformed(parseInt(cl.getOptionValue("async"),
                    "illegal argument of option -async", 0, 0xffff));

        ArrayList<TransferCapability> tc = new ArrayList<TransferCapability>();
        tc.add(new TransferCapability(UID.VerificationSOPClass, ONLY_DEF_TS,
                TransferCapability.SCP));
        if (cl.hasOption("mwl"))
            dcmof.registerMWLSCP(new File(cl.getOptionValue("mwl")), tc);
        if (cl.hasOption("mpps"))
            dcmof.registerMPPSSCP(new File(cl.getOptionValue("mpps")), tc);
        if (cl.hasOption("mppsxml"))
            dcmof.registerMPPSXMLSCP(new File(cl.getOptionValue("mppsxml")), tc);
        if (cl.hasOption("ian"))
            dcmof.registerIANSCP(new File(cl.getOptionValue("ian")), tc);
        if (cl.hasOption("ianxml"))
            dcmof.registerIANXMLSCP(new File(cl.getOptionValue("ianxml")), tc);
        if (cl.hasOption("scn"))
            dcmof.registerSCNSCP(new File(cl.getOptionValue("scn")), tc);
        if (cl.hasOption("scnxml"))
            dcmof.registerSCNXMLSCP(new File(cl.getOptionValue("scnxml")), tc);
        dcmof.setComments(cl.hasOption("C"));
        dcmof.setIndent(!cl.hasOption("c"));

        dcmof.setTransferCapability(tc
                .toArray(new TransferCapability[tc.size()]));
        if (cl.hasOption("tls")) {
            String cipher = cl.getOptionValue("tls");
            if ("NULL".equalsIgnoreCase(cipher)) {
                dcmof.setTlsWithoutEncyrption();
            } else if ("3DES".equalsIgnoreCase(cipher)) {
                dcmof.setTls3DES_EDE_CBC();
            } else if ("AES".equalsIgnoreCase(cipher)) {
                dcmof.setTlsAES_128_CBC();
            } else {
                exit("Invalid parameter for option -tls: " + cipher);
            }
            if (cl.hasOption("tls1")) {
                dcmof.setTlsProtocol(TLS1);
            } else if (cl.hasOption("ssl3")) {
                dcmof.setTlsProtocol(SSL3);
            } else if (cl.hasOption("no_tls1")) {
                dcmof.setTlsProtocol(NO_TLS1);
            } else if (cl.hasOption("no_ssl3")) {
                dcmof.setTlsProtocol(NO_SSL3);
            } else if (cl.hasOption("no_ssl2")) {
                dcmof.setTlsProtocol(NO_SSL2);
            }
            dcmof.setTlsNeedClientAuth(!cl.hasOption("noclientauth"));

            if (cl.hasOption("keystore")) {
                dcmof.setKeyStoreURL(cl.getOptionValue("keystore"));
            }
            if (cl.hasOption("keystorepw")) {
                dcmof.setKeyStorePassword(
                        cl.getOptionValue("keystorepw"));
            }
            if (cl.hasOption("keypw")) {
                dcmof.setKeyPassword(cl.getOptionValue("keypw"));
            }
            if (cl.hasOption("truststore")) {
                dcmof.setTrustStoreURL(
                        cl.getOptionValue("truststore"));
            }
            if (cl.hasOption("truststorepw")) {
                dcmof.setTrustStorePassword(
                        cl.getOptionValue("truststorepw"));
            }
            try {
                dcmof.initTLS();
            } catch (Exception e) {
                System.err.println("ERROR: Failed to initialize TLS context:"
                        + e.getMessage());
                System.exit(2);
            }
        }        
        try {
            dcmof.start();
            return dcmof;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void setIndent(boolean b) {
        this.indent = b;
        
    }

    private void setComments(boolean b) {
        this.comments = b;
    }

    private void setTransferSyntax(String[] tsuids) {
        this.tsuids = tsuids;
    }

    private void registerMWLSCP(File dir, ArrayList<TransferCapability> tc) {
        MWLSCP mwlscp = new MWLSCP(executor, this);
        mwlscp.setSource(dir);
        ae.register(mwlscp);
        tc.add(new TransferCapability(mwlscp.getSopClass(), tsuids,
                TransferCapability.SCP));
    }

    private void registerMPPSSCP(File dir, ArrayList<TransferCapability> tc) {
        register(new MPPSSCP(this), dir, tc);
    }

    private void registerMPPSXMLSCP(File dir, ArrayList<TransferCapability> tc) {
        register(new MPPSSCP.XML(this), dir, tc);
    }

    private void register(MPPSSCP mppsscp, File dir, ArrayList<TransferCapability> tc) {
        mppsscp.setDestination(dir);
        ae.register(mppsscp.getNCreateSCP());
        ae.register(mppsscp.getNSetSCP());
        tc.add(new TransferCapability(mppsscp.getNCreateSCP().getSopClass(),
                tsuids, TransferCapability.SCP));
    }

    private void registerIANXMLSCP(File dir, ArrayList<TransferCapability> tc) {
        register(new IANSCP.XML(this), dir, tc);

    }

    private void registerIANSCP(File dir, ArrayList<TransferCapability> tc) {
        register(new IANSCP(this), dir, tc);
    }

    private void register(IANSCP ianscp, File dir, ArrayList<TransferCapability> tc) {
        ianscp.setDestination(dir);
        ae.register(ianscp);
        tc.add(new TransferCapability(ianscp.getSopClass(), tsuids,
                TransferCapability.SCP));
    }

    private void registerSCNSCP(File dir, ArrayList<TransferCapability> tc) {
        register(new SCNSCP(this), dir, tc);
    }

    private void registerSCNXMLSCP(File dir, ArrayList<TransferCapability> tc) {
        register(new SCNSCP.XML(this), dir, tc);
    }

    private void register(SCNSCP scnscp, File dir, ArrayList<TransferCapability> tc) {
        scnscp.setDestination(dir);
        ae.register(scnscp);
        tc.add(new TransferCapability(scnscp.getSopClass(), tsuids,
                TransferCapability.SCP));
    }

    private void setTransferCapability(TransferCapability[] tc) {
        ae.setTransferCapability(tc);
    }

    private void setMaxOpsPerformed(int maxOps) {
        ae.setMaxOpsPerformed(maxOps);
    }

    public void start() throws IOException {
        device.startListening(executor);
        LOG.info("Order filler server listening on port " + nc.getPort());
    }
    
    public void stop() {
    	if (device != null)
    	device.stopListening();
    	LOG.info("Order filler server stopped on "+ nc.getPort());
	}

    private static String[] split(String s, char delim, int defPos) {
        String[] s2 = new String[2];
        s2[defPos] = s;
        int pos = s.indexOf(delim);
        if (pos != -1) {
            s2[0] = s.substring(0, pos);
            s2[1] = s.substring(pos + 1);
        }
        return s2;
    }

    private static void exit(String msg) {
        System.err.println(msg);
        System.err.println("Try 'dcmof -h' for more information.");
        System.exit(1);
    }
    
    private static int parseInt(String s, String errPrompt, int min, int max) {
        try {
            int i = Integer.parseInt(s);
            if (i >= min && i <= max)
                return i;
        } catch (NumberFormatException e) {
            // parameter is not a valid integer; fall through to exit
        }
        exit(errPrompt);
        throw new RuntimeException();
    }

    void storeAsXML(File f, DicomObject data) throws Exception {
        LOG.info("M-WRITE " + f);
        SAXTransformerFactory tf = 
                (SAXTransformerFactory) TransformerFactory.newInstance();
        TransformerHandler th = tf.newTransformerHandler();
        if (indent)
            th.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
        
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            th.setResult(new StreamResult(fos));
            new SAXWriter(th, comments ? th : null).write(data);
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (Exception ioe) {
                    // ignore
                }
            }
        }
    }

    void storeAsDICOM(File f, DicomObject data) throws Exception {
        LOG.info("M-WRITE " + f);
        DicomOutputStream out = new DicomOutputStream(new FileOutputStream(f));
        try {
            out.writeDicomFile(data);
        } finally {
            CloseUtils.safeClose(out);
        }
    }

    DicomObject load(File f) throws Exception {
        LOG.info("M-READ " + f);
        return f.getName().endsWith(".xml") ? loadXML(f) : loadDICOM(f);
    }

    private DicomObject loadDICOM(File f) throws Exception {
        DicomInputStream in = new DicomInputStream(f);
        try {
            return in.readDicomObject();
        } finally {
            in.close();
        }
    }

    private DicomObject loadXML(File f) throws Exception {
        DicomObject dcmobj = new BasicDicomObject();
        SAXParser p = SAXParserFactory.newInstance().newSAXParser();
        ContentHandlerAdapter ch = new ContentHandlerAdapter(dcmobj);
        p.parse(f, ch);
        return dcmobj;
    }

    public void initTLS() throws GeneralSecurityException, IOException {
        KeyStore keyStore = loadKeyStore(keyStoreURL, keyStorePassword);
        KeyStore trustStore = loadKeyStore(trustStoreURL, trustStorePassword);
        device.initTLS(keyStore,
                keyPassword != null ? keyPassword : keyStorePassword,
                trustStore);
    }
    
    private static KeyStore loadKeyStore(String url, char[] password)
            throws GeneralSecurityException, IOException {
        KeyStore key = KeyStore.getInstance(toKeyStoreType(url));
        InputStream in = openFileOrURL(url);
        try {
            key.load(in, password);
        } finally {
            in.close();
        }
        return key;
    }

    private static InputStream openFileOrURL(String url) throws IOException {
        if (url.startsWith("resource:")) {
            return DcmOF.class.getClassLoader().getResourceAsStream(
                    url.substring(9));
        }
        try {
            return new URL(url).openStream();
        } catch (MalformedURLException e) {
            return new FileInputStream(url);
        }
    }

    private static String toKeyStoreType(String fname) {
        return fname.endsWith(".p12") || fname.endsWith(".P12")
                 ? "PKCS12" : "JKS";
    }
}
