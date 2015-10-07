/*
 * Copyright (c) 2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.portal.service.template;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.neuland.jade4j.template.TemplateLoader;

/**
 * Template loader that can deliver template from a String, or delegate
 * to another TemplateLoader.
 * 
 * The String must be set before each use
 * 
 */
final class DelegatingStringTemplateLoader implements TemplateLoader
{

    /** name of the String template */
    static final String TEMPLATE_NAME = "__STRING_TEMPLATE__.jade";
    /** Storage for the String template in the current Thread */
    private final ThreadLocal<String> _strTemplate;
    /** delegate TemplateLoader */
    private final TemplateLoader _delegate;
    
    /**
     * Constructor
     * @param delegate the TemplateLoader to delegate to if not using the String template
     */
    DelegatingStringTemplateLoader( TemplateLoader delegate )
    {
        _delegate = delegate;
        _strTemplate = new ThreadLocal<String>(  );
    }
    
    /**
     * Sets the String template
     * @param template the String template
     */
    void setStringTemplate( String template )
    {
        _strTemplate.set( template );
    }
    
    @Override
    public long getLastModified( String name ) throws IOException
    {
        if ( TEMPLATE_NAME.equals( name ) )
        {
            String strTemplate = _strTemplate.get(  );
            if ( strTemplate == null )
            {
                throw new IllegalStateException( "setTemplate must be called first on this thread" );
            }
            try
            {
                // Allow caching of String templates. Hopefully there will be few to no collisions
                MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
                byte[] sha1 = digest.digest( strTemplate.getBytes( Charset.forName( "UTF-8" ) ) );
                return (long)(
                        (long)(0xff & sha1[0]) << 56  |
                        (long)(0xff & sha1[1]) << 48  |
                        (long)(0xff & sha1[2]) << 40  |
                        (long)(0xff & sha1[3]) << 32  |
                        (long)(0xff & sha1[4]) << 24  |
                        (long)(0xff & sha1[5]) << 16  |
                        (long)(0xff & sha1[6]) << 8   |
                        (long)(0xff & sha1[7]) << 0
                        );
            } catch ( NoSuchAlgorithmException e )
            {
                // should not happen
                throw new RuntimeException( e );
            }
        }
        return _delegate.getLastModified( name );
    }

    @Override
    public Reader getReader( String name ) throws IOException
    {
        if ( TEMPLATE_NAME.equals( name ) )
        {
            if ( _strTemplate.get(  ) == null )
            {
                throw new IllegalStateException( "setTemplate must be called first on this thread" );
            }
            Reader reader = new StringReader( _strTemplate.get(  ) );
            _strTemplate.remove(  );
            return reader;
        }
        return _delegate.getReader( name );
    }
    
}