package org.jbpm.rest_task;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.logger.KieRuntimeLogger;

public class Listener extends DefaultAgendaEventListener implements KieRuntimeLogger {

	private String fileName;
	private List< String > messages = new ArrayList< String >();

	public Listener( String fileName ) {
		this.fileName = fileName;
	}

	public void afterMatchFired( AfterMatchFiredEvent event ) {
		messages.add( event.getMatch().getRule().getName() + "\n");
	}

	public void afterRuleFlowGroupActivated( RuleFlowGroupActivatedEvent event ) {
		messages.add( "\n=================================================================\n" );
		messages.add( "Rule Flow Group: " + event.getRuleFlowGroup().getName() + "\n");
		messages.add( "=================================================================\n" );
		messages.add( "The following rules fired...\n\n" );
	}

	public void close() {
		try {
			FileWriter writer = new FileWriter( fileName );

			for ( String m : messages ) {
				writer.write( m );
			}

			writer.close();
		} catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
