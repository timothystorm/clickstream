package org.storm.clickstream;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ClickStreamFilterTest.class, ClickStreamLifeCycleTest.class, ClickStreamListenerTest.class })
public class _ClickStreamSuite {}
