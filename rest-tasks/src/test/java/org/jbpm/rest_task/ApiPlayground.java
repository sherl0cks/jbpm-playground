/* 
 * Written by Red Hat Consulting.	
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.rest_task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

/**
 * Note that the pom.xml has a logback added. BRMS 6 now binds this to slf4j and logs everything, which is super
 * helpful!
 * 
 * @author sherl0ck
 * 
 */
public class ApiPlayground {

	@SuppressWarnings("unchecked")
	@Test
	public void test() {

		// Get a KieContainer from the classpath and make sure it ain't null
		// Note kmodule.xml in META-INF
		KieContainer kContainer = KieServices.Factory.get().newKieClasspathContainer();
		Assert.assertNotNull( kContainer );

		// Get the KieBase and print out the rules in it and assert that it's the number we expect (1 in this example)
		Collection<Rule> rules = new ArrayList<Rule>();
		for ( KiePackage p : kContainer.getKieBase().getKiePackages() ) {
			rules.addAll( p.getRules() );
		}
		Assert.assertEquals( 1, rules.size() );

		System.out.println( "\nRules in the KBase" );
		for ( Rule r : rules ) {
			System.out.println( r.getName() );
		}
		System.out.println( "End rules in KBase \n" );

		// Grab a stateless session from the container and work with the batch api like 5.x
		StatelessKieSession session = kContainer.newStatelessKieSession();

		List<Command<ExecutionResults>> commands = new ArrayList<Command<ExecutionResults>>();

		commands.add( CommandFactory.newInsertElements( Arrays.asList( "Justin", "Jeff", "Jimmy" ) ) );
		commands.add( CommandFactory.newFireAllRules() );

		System.out.println( "Execution 1 \n" );
		session.execute( CommandFactory.newBatchExecution( commands ) );
		System.out.println( "\nEnd Execution 1 \n" );

		Assert.assertNotNull( session );

		// Turns out Stateless Sessions are resuable - we don't need to create new instances like we do in the component
		System.out.println( "Execution 2 \n" );
		session.execute( CommandFactory.newBatchExecution( commands ) );
		System.out.println( "\nEnd Execution 2 \n" );
	}

}
