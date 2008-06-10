package org.londonwicket.app;

import junit.framework.TestCase;
import org.apache.wicket.util.tester.WicketTester;
import org.londonwicket.pages.GoogleMapsPage;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage extends TestCase
{
	private WicketTester tester;

	public void setUp()
	{
		tester = new WicketTester();
	}

	public void testRenderMyPage()
	{
		//start and render the test page
		tester.startPage(GoogleMapsPage.class);

		//assert rendered page class
		tester.assertRenderedPage(GoogleMapsPage.class);

		//assert rendered label component
		tester.assertLabel("message", "If you see this message wicket is properly configured and running");
	}
}
