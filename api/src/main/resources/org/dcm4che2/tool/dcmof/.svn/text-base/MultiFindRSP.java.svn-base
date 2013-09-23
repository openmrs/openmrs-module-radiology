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

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.Status;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Revision: 13902 $ $Date: 2010-08-19 16:39:45 +0200 (Thu, 19 Aug 2010) $
 * @since Mar 11, 2006
 *
 */
class MultiFindRSP implements DimseRSP {

    private final DcmOF dcmOF;
    private File[] files;
    private int cur = 0;
    private DicomObject rsp;
    private DicomObject keys;
    private DicomObject mwl;

    public MultiFindRSP(DcmOF dcmOF, DicomObject keys, DicomObject rsp, File source) {
        this.dcmOF = dcmOF;
        this.keys = keys;
        // always return Specific Character Set
        if (!keys.contains(Tag.SpecificCharacterSet))
            keys.putNull(Tag.SpecificCharacterSet, VR.CS);
        this.rsp = rsp;
        this.files = source.listFiles();
        if (this.files == null) {
            this.files = new File[0];
        }
    }

    public synchronized boolean next() {
        if (cur < 0)
            return false;
        if (files == null) {
            rsp.putInt(Tag.Status, VR.US, Status.Cancel);
        } else {
            try {
                while (cur < files.length) {
                    mwl = dcmOF.load(files[cur++]);
                    if (mwl.matches(keys, true)) {
                        // always return Specific Character Set
                        if (!mwl.contains(Tag.SpecificCharacterSet))
                            mwl.putNull(Tag.SpecificCharacterSet, VR.CS);
                        rsp.putInt(Tag.Status, VR.US, mwl.containsAll(keys) 
                                ? Status.Pending : Status.PendingWarning);
                        return true;
                    }
                }
                rsp.putInt(Tag.Status, VR.US, Status.Success);
            } catch (Exception e) {
                rsp.putInt(Tag.Status, VR.US, Status.ProcessingFailure);
                rsp.putString(Tag.ErrorComment, VR.LO, e.getMessage());
            }
        }
        mwl = null;
        cur = -1;
        return true;
    }

    public DicomObject getCommand() {
        return rsp;
    }

    public DicomObject getDataset() {
        return mwl != null ? mwl.subSet(keys) : null;
    }

    public synchronized void cancel(Association a) {
        files = null;
    }

}
