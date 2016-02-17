/*************************************************************************
 *                                                                       *
 *  EJBCA Community: The OpenSource Certificate Authority                *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/

package org.ejbca.core.ejb.hardtoken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.cesecore.authentication.tokens.AuthenticationSubject;
import org.cesecore.authentication.tokens.AuthenticationToken;
import org.cesecore.authentication.tokens.UsernamePrincipal;
import org.cesecore.authorization.AuthorizationDeniedException;
import org.cesecore.authorization.rules.AccessRuleData;
import org.cesecore.authorization.rules.AccessRuleState;
import org.cesecore.authorization.user.AccessMatchType;
import org.cesecore.authorization.user.AccessUserAspectData;
import org.cesecore.authorization.user.matchvalues.X500PrincipalAccessMatchValue;
import org.cesecore.certificates.certificateprofile.CertificateProfileConstants;
import org.cesecore.mock.authentication.SimpleAuthenticationProviderSessionRemote;
import org.cesecore.mock.authentication.tokens.TestAlwaysAllowLocalAuthenticationToken;
import org.cesecore.mock.authentication.tokens.TestX509CertificateAuthenticationToken;
import org.cesecore.roles.RoleData;
import org.cesecore.roles.management.RoleManagementSessionRemote;
import org.cesecore.util.CertTools;
import org.cesecore.util.CryptoProviderTools;
import org.cesecore.util.EjbRemoteHelper;
import org.ejbca.core.model.authorization.AccessRulesConstants;
import org.ejbca.core.model.hardtoken.HardTokenProfileExistsException;
import org.ejbca.core.model.hardtoken.profiles.EnhancedEIDProfile;
import org.ejbca.core.model.hardtoken.profiles.HardTokenProfile;
import org.ejbca.core.model.hardtoken.profiles.SwedishEIDProfile;
import org.ejbca.core.model.hardtoken.profiles.TurkishEIDProfile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the hard token profile entity bean.
 * 
 * @version $Id: HardTokenProfileTest.java 19902 2014-09-30 14:32:24Z anatom $
 */
public class HardTokenProfileTest {
    private static Logger log = Logger.getLogger(HardTokenProfileTest.class);

    private HardTokenSessionRemote hardTokenSession = EjbRemoteHelper.INSTANCE.getRemoteSession(HardTokenSessionRemote.class);
    private RoleManagementSessionRemote roleManagementSession = EjbRemoteHelper.INSTANCE.getRemoteSession(RoleManagementSessionRemote.class);
    private SimpleAuthenticationProviderSessionRemote simpleAuthenticationProvider = EjbRemoteHelper.INSTANCE.getRemoteSession(SimpleAuthenticationProviderSessionRemote.class, EjbRemoteHelper.MODULE_TEST);

    private static int SVGFILESIZE = 512 * 1024; // 1/2 Mega char

    private static final AuthenticationToken internalAdmin = new TestAlwaysAllowLocalAuthenticationToken(new UsernamePrincipal("HardTokenProfileTest"));

    @Before
    public void setUp() throws Exception {
        CryptoProviderTools.installBCProvider();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test01AddHardTokenProfile() throws HardTokenProfileExistsException, AuthorizationDeniedException {

        final SwedishEIDProfile swedishProfileOrg = new SwedishEIDProfile();
        final EnhancedEIDProfile enhancedProfileOrg = new EnhancedEIDProfile();
        final TurkishEIDProfile turkishProfileOrg = new TurkishEIDProfile();

        final String svgdata = createSVGData();
        swedishProfileOrg.setPINEnvelopeData(svgdata);
        enhancedProfileOrg.setIsKeyRecoverable(EnhancedEIDProfile.CERTUSAGE_ENC, true);
        // It should be possible to indicate that a certificate should not be generated by not specifying a cert profile for this key. 
        swedishProfileOrg.setCertificateProfileId(SwedishEIDProfile.CERTUSAGE_SIGN, CertificateProfileConstants.CERTPROFILE_NO_PROFILE);

        this.hardTokenSession.addHardTokenProfile(internalAdmin, "SWETEST", swedishProfileOrg);
        this.hardTokenSession.addHardTokenProfile(internalAdmin, "ENHTEST", enhancedProfileOrg);
        this.hardTokenSession.addHardTokenProfile(internalAdmin, "TURTEST", turkishProfileOrg);

        final Collection<Integer> authorizedHardTokenIds = this.hardTokenSession.getAuthorizedHardTokenProfileIds(internalAdmin);

        final SwedishEIDProfile swedishProfile = (SwedishEIDProfile) this.hardTokenSession.getHardTokenProfile("SWETEST");
        final EnhancedEIDProfile enhancedProfile = (EnhancedEIDProfile) this.hardTokenSession.getHardTokenProfile("ENHTEST");
        final TurkishEIDProfile turkishProfile = (TurkishEIDProfile) this.hardTokenSession.getHardTokenProfile("TURTEST");

        final String svgdata2 = swedishProfile.getPINEnvelopeData();

        assertTrue(  "Profile not authorized", authorizedHardTokenIds.contains(Integer.valueOf(this.hardTokenSession.getHardTokenProfileId("SWETEST")) )  );
        assertTrue("Saving certificate profile failed", swedishProfile.getCertificateProfileId(SwedishEIDProfile.CERTUSAGE_SIGN)==CertificateProfileConstants.CERTPROFILE_NO_PROFILE);
        assertTrue("Saving SVG Data failed", svgdata.equals(svgdata2));
        assertTrue("Saving Hard Token Profile failed", enhancedProfile.getIsKeyRecoverable(EnhancedEIDProfile.CERTUSAGE_ENC));
        assertTrue("Saving Turkish Hard Token Profile failed", turkishProfile!=null);
    }

    @Test
    public void test02RenameHardTokenProfile() throws Exception {
        log.trace(">test02RenameHardTokenProfile()");

        boolean ret = false;
        try {
            hardTokenSession.renameHardTokenProfile(internalAdmin, "SWETEST", "SWETEST2");
            ret = true;
        } catch (HardTokenProfileExistsException pee) {
            log.debug("", pee);
        }
        assertTrue("Renaming Hard Token Profile failed", ret);

        log.trace("<test02RenameHardTokenProfile()");
    }

    @Test
    public void test03CloneHardTokenProfile() throws Exception {
        log.trace(">test03CloneHardTokenProfile()");

        boolean ret = false;
        try {
            hardTokenSession.cloneHardTokenProfile(internalAdmin, "SWETEST2", "SWETEST");
            ret = true;
        } catch (HardTokenProfileExistsException pee) {
            log.debug("", pee);
        }
        assertTrue("Cloning Hard Token Profile failed", ret);

        log.trace("<test03CloneHardTokenProfile()");
    }

    @Test
    public void test04EditHardTokenProfile() throws Exception {
        log.trace(">test04EditHardTokenProfile()");
        boolean ret = false;
        HardTokenProfile profile = hardTokenSession.getHardTokenProfile("ENHTEST");
        profile.setHardTokenSNPrefix("11111");
        hardTokenSession.changeHardTokenProfile(internalAdmin, "ENHTEST", profile);
        ret = true;
        assertTrue("Editing HardTokenProfile failed", ret);
        log.trace("<test04EditHardTokenProfile()");
    }

    @Test
    public void test05removeHardTokenProfiles() throws Exception {
        log.trace(">test05removeHardTokenProfiles()");
        boolean ret = false;
        try {
            // Remove all except ENHTEST
            hardTokenSession.removeHardTokenProfile(internalAdmin, "SWETEST");
            hardTokenSession.removeHardTokenProfile(internalAdmin, "SWETEST2");
            hardTokenSession.removeHardTokenProfile(internalAdmin, "ENHTEST");
            hardTokenSession.removeHardTokenProfile(internalAdmin, "TURTEST");
            ret = true;
        } catch (Exception pee) {
            log.debug("", pee);
        }
        assertTrue("Removing Hard Token Profile failed", ret);
        log.trace("<test05removeHardTokenProfiles()");
    }

    @Test
    public void testIsAuthorizedToHardTokenProfile() throws Exception {
        TestX509CertificateAuthenticationToken admin = (TestX509CertificateAuthenticationToken) simpleAuthenticationProvider
                .authenticate(new AuthenticationSubject(null, null));

        int caid = CertTools.getIssuerDN(admin.getCertificate()).hashCode();
        String cN = CertTools.getPartFromDN(CertTools.getIssuerDN(admin.getCertificate()), "CN");
        String rolename = "testGetAuthorizedToHardTokenProfile";
        final String alias = "spacemonkeys";
        try {
            RoleData role = roleManagementSession.create(internalAdmin, rolename);
            Collection<AccessUserAspectData> subjects = new ArrayList<AccessUserAspectData>();
            subjects.add(new AccessUserAspectData(rolename, caid, X500PrincipalAccessMatchValue.WITH_COMMONNAME, AccessMatchType.TYPE_EQUALCASE, cN));
            role = roleManagementSession.addSubjectsToRole(internalAdmin, role, subjects);
            Collection<AccessRuleData> accessRules = new ArrayList<AccessRuleData>();
            accessRules.add(new AccessRuleData(rolename, AccessRulesConstants.HARDTOKEN_ISSUEHARDTOKENS, AccessRuleState.RULE_ACCEPT, false));
            role = roleManagementSession.addAccessRulesToRole(internalAdmin, role, accessRules);
            HardTokenProfile profile = new SwedishEIDProfile();
            hardTokenSession.addHardTokenProfile(internalAdmin, alias, profile);
            
            // Test authorization to edit with an unauthorized admin
            try {
                hardTokenSession.addHardTokenProfile(admin, alias, profile);
                fail("admin should not have been authorized to edit profile");
            } catch (AuthorizationDeniedException e) {
                assertEquals("Administrator is not authorized to resource /hardtoken_functionality/edit_hardtoken_profiles. Msg: .", e.getMessage());
            }
            // Test authorization to edit with an unauthorized admin
            try {
                hardTokenSession.changeHardTokenProfile(admin, alias, profile);
                fail("admin should not have been authorized to edit profile");
            } catch (AuthorizationDeniedException e) {
                assertEquals("Administrator is not authorized to resource /hardtoken_functionality/edit_hardtoken_profiles. Msg: .", e.getMessage());
            }
            // Test authorization to edit with an unauthorized admin
            try {
                hardTokenSession.cloneHardTokenProfile(admin, alias, "newmonkeys");
                fail("admin should not have been authorized to edit profile");
            } catch (AuthorizationDeniedException e) {
                assertEquals("Administrator is not authorized to resource /hardtoken_functionality/edit_hardtoken_profiles. Msg: .", e.getMessage());
            }
            // Test authorization to edit with an unauthorized admin
            try {
                hardTokenSession.removeHardTokenProfile(admin, alias);
                fail("admin should not have been authorized to edit profile");
            } catch (AuthorizationDeniedException e) {
                assertEquals("Administrator is not authorized to resource /hardtoken_functionality/edit_hardtoken_profiles. Msg: .", e.getMessage());
            }
            // Test authorization to edit with an unauthorized admin
            try {
                hardTokenSession.renameHardTokenProfile(admin, alias, "renamedmonkey");
                fail("admin should not have been authorized to edit profile");
            } catch (AuthorizationDeniedException e) {
                assertEquals("Administrator is not authorized to resource /hardtoken_functionality/edit_hardtoken_profiles. Msg: .", e.getMessage());
            }

        } finally {
            hardTokenSession.removeHardTokenProfile(internalAdmin, alias);
            roleManagementSession.remove(internalAdmin, rolename);
        }
    }

    private String createSVGData() {
        char[] chararray = new char[SVGFILESIZE];
        Arrays.fill(chararray, 'a');

        return new String(chararray);
    }

}
