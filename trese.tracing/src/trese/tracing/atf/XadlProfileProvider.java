/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.tracing.atf;

import net.ample.tracing.core.RepositoryProfileDocumentProvider;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * Needed because defining a <resource> doesn't work.
 * 
 * @author Michiel Hendriks
 */
public class XadlProfileProvider implements RepositoryProfileDocumentProvider
{
	public XadlProfileProvider()
	{}

	public Document getProfileDocument()
	{
		SAXReader rd = new SAXReader();
		try
		{
			return rd.read(XadlProfileProvider.class.getResourceAsStream("xadl-profile.xml"));
		}
		catch (DocumentException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
