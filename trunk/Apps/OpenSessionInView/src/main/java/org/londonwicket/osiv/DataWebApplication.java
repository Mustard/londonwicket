package org.londonwicket.osiv;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.londonwicket.osiv.jpa.PersistenceUnit;



public abstract class DataWebApplication extends WebApplication {
	
	public DataWebApplication(){
		PersistenceUnit.unitName = getUnitName();
	}
	
	protected abstract String getUnitName();


	@Override
	public RequestCycle newRequestCycle(Request request, Response response) {
		return new DataRequestCycle(this,
				(WebRequest) request, response);
	}
	
}
