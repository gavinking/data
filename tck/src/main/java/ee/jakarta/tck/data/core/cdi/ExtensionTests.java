/*
 * Copyright (c) 2023, 2024 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package ee.jakarta.tck.data.core.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import ee.jakarta.tck.data.common.cdi.Directory;
import ee.jakarta.tck.data.common.cdi.Person;
import ee.jakarta.tck.data.core.cdi.provider.DirectoryBuildCompatibleExtension;
import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.CDI;
import ee.jakarta.tck.data.framework.junit.anno.Core;
import jakarta.enterprise.inject.build.compatible.spi.BuildCompatibleExtension;
import jakarta.inject.Inject;

@Core
@AnyEntity
@CDI
public class ExtensionTests {
    @Deployment
    public static WebArchive createDeployment() {
        JavaArchive provider = ShrinkWrap.create(JavaArchive.class)
                .addPackage(DirectoryBuildCompatibleExtension.class.getPackage())
                .addAsServiceProvider(BuildCompatibleExtension.class, DirectoryBuildCompatibleExtension.class);
                
        
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(ExtensionTests.class.getPackage())
                .addPackage(Person.class.getPackage())
                .addAsLibraries(provider);

    }
    
    @Inject
    Directory directory;
    
    @Assertion(id = "133", strategy = "Verifies ability for a CDI BuildCompatibleExtension to handle custom entity annotations")
    public void testDataProviderWithBuildCompatibleExtension() {
        assertEquals(List.of("Skinner", "Powell", "Gibson"), directory.findLastNameByIdInOrderByAgeDesc(List.of(04L, 05L, 011L)));
    }
    
    @Assertion(id = "640", strategy = "Verifies that another Jakarta Data Provider does not attempt to implement the Dictonary repository")
    public void testDataRepositoryHonorsProviderAttribute() {
        long id = 013L;
        Person original = new Person(id, "Mark", "Pearson", 46);
        Person updated = new Person(id, "Mark", "Pearson", 45);
        
        try {
            assertEquals(null, directory.putPerson(original));
            assertEquals(original, directory.putPerson(updated));
        } finally {
            directory.deleteById(id);
        }
    }
}
