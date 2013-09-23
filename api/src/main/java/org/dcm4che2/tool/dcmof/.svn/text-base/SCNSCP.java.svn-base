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
import java.io.IOException;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.PDVInputStream;
import org.dcm4che2.net.Status;
import org.dcm4che2.net.service.StorageService;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Revision: 5544 $ $Date: 2007-11-26 14:50:17 +0100 (Mon, 26 Nov 2007) $
 * @since Feb 2, 2006
 *
 */
class SCNSCP extends StorageService
{
    protected final DcmOF dcmOF;
    protected File destination;

    public SCNSCP(DcmOF dcmOF)
    {
        super(UID.BasicStudyContentNotificationSOPClassRetired);
        this.dcmOF = dcmOF;
    }

    public final void setDestination(File destination)
    {
        destination.mkdirs();
        this.destination = destination;
    }
    
    @Override
    protected void doCStore(Association as, int pcid, DicomObject rq,
            PDVInputStream dataStream, String tsuid, DicomObject rsp)
            throws DicomServiceException, IOException
    {
        DicomObject data = dataStream.readDataset();
        String iuid = rq.getString(Tag.AffectedSOPInstanceUID);
        data.initFileMetaInformation(UID.BasicStudyContentNotificationSOPClassRetired,
                iuid, UID.ExplicitVRLittleEndian);
        try {
            store(iuid, data);
        } catch (Exception e) {
            throw new DicomServiceException(rq, Status.ProcessingFailure,
                    e.getMessage());
        }
    }
 
    protected void store(String iuid, DicomObject data) throws Exception {
        dcmOF.storeAsDICOM(new File(destination, iuid), data);
    }
    
    static class XML extends SCNSCP {

        public XML(DcmOF dcmOF) {
            super(dcmOF);
        }
        
        @Override
        protected void store(String iuid, DicomObject data) throws Exception {
            dcmOF.storeAsXML(new File(destination, iuid + ".xml"), data);
        }
    }
}
