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
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

/**
 * 
 * Test cases for cases 01128215
 * 
 * @author sherl0ck
 * 
 */
public class AuditLogBug {

	/**
	 * This test will create an audit log that JBDS 7.1.1 cannot read (thread and regular). Please
	 * note it uses rule flow/bpmn2 (line 72). Console logging works as expected.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void createsBadAuditLog() {

		KieContainer kContainer = KieServices.Factory.get().newKieClasspathContainer();
		Assert.assertNotNull( kContainer );

		Collection< Rule > rules = new ArrayList< Rule >();
		for ( KiePackage p : kContainer.getKieBase().getKiePackages() ) {
			rules.addAll( p.getRules() );
		}
		Assert.assertEquals( 3, rules.size() );

		StatelessKieSession session = kContainer.newStatelessKieSession();

		KieRuntimeLogger thread = KieServices.Factory.get().getLoggers().newFileLogger( session, "thread_fail" );
		KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newFileLogger( session, "audit_fail" );
		KieRuntimeLogger console = KieServices.Factory.get().getLoggers().newConsoleLogger( session );

		List< Command< ExecutionResults >> commands = new ArrayList< Command< ExecutionResults >>();

		commands.add( CommandFactory.newInsertElements( Arrays.asList( "Justin", "Jeff", "Jimmy" ) ) );
		commands.add( CommandFactory.newStartProcess( "defaultPackage.exampleRuleFlow" ) ); // This is the big difference here
		commands.add( CommandFactory.newFireAllRules() );

		session.execute( CommandFactory.newBatchExecution( commands ) );

		Assert.assertNotNull( session );

		logger.close();
		console.close();
		thread.close();
		
	}
	
	/**
	 * This test will create an audit log that JBDS 7.1.1 can read (thread and file). Please
	 * note this test does not invoke rule flow/bpmn2 (line 110).
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void createsGoodAuditLog() {
		KieContainer kContainer = KieServices.Factory.get().newKieClasspathContainer();
		Assert.assertNotNull( kContainer );

		Collection< Rule > rules = new ArrayList< Rule >();
		for ( KiePackage p : kContainer.getKieBase().getKiePackages() ) {
			rules.addAll( p.getRules() );
		}
		Assert.assertEquals( 3, rules.size() );

		StatelessKieSession session = kContainer.newStatelessKieSession();

		KieRuntimeLogger thread = KieServices.Factory.get().getLoggers().newFileLogger( session, "thread_success" );
		KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newFileLogger( session, "audit_success" );
		KieRuntimeLogger console = KieServices.Factory.get().getLoggers().newConsoleLogger( session );

		List< Command< ExecutionResults >> commands = new ArrayList< Command< ExecutionResults >>();

		commands.add( CommandFactory.newInsertElements( Arrays.asList( "Justin", "Jeff", "Jimmy" ) ) );
		//commands.add( CommandFactory.newStartProcess( "defaultPackage.exampleRuleFlow" ) ); // This is the big difference here
		commands.add( CommandFactory.newFireAllRules() );

		session.execute( CommandFactory.newBatchExecution( commands ) );

		Assert.assertNotNull( session );

		logger.close();
		console.close();
		thread.close();
	}
}
