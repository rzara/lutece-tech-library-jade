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
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.paris.lutece.util.html.HtmlTemplate;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Freemarker directive for processing its body with the jade template engine.
 * 
 * The root model of the freemarker template is passed down to the jade template.
 * The body is first interpreted by freemarker, so jade String interpolation should
 * be escaped like so:
 * 
 * <code>${r"#{jade_var}"}</code>
 */
public class FreemarkerDirective implements TemplateDirectiveModel
{

    private static final Logger _logger = Logger.getLogger( "lutece.jade" );
    
    private final IJadeTemplateService _jadeTemplateService;
    
    /**
     * Constructor
     * @param jadeTemplateService the jade template service
     */
    FreemarkerDirective( IJadeTemplateService jadeTemplateService )
    {
        _jadeTemplateService = jadeTemplateService;
    }
    
    @Override
    public void execute( Environment env, @SuppressWarnings( "rawtypes" ) Map params, TemplateModel[ ] loopVars, TemplateDirectiveBody body )
            throws TemplateException, IOException
    {
        StringWriter bodyOut = new StringWriter(  );
        body.render( bodyOut );
        _logger.debug( bodyOut.toString( ) );
        HtmlTemplate res = _jadeTemplateService.loadTemplate( bodyOut.toString(  ), env.getLocale(  ), getModel( env ) );
        env.getOut(  ).write( res.getHtml(  ) );
    }

    /**
     * Construct a jade model from a freemarker model
     * @param env the freemarker processing environment
     * @return a jade model
     */
    private Map<String, Object> getModel( final Environment env )
    {
        final TemplateHashModel freemarkerModel = env.getDataModel(  );
        return new Map<String, Object>(  ) {

            @Override
            public int size( )
            {
                try
                {
                    return env.getKnownVariableNames( ).size( );
                } catch ( TemplateModelException e )
                {
                    throw new RuntimeException( e );
                }
            }

            @Override
            public boolean isEmpty( )
            {
                try
                {
                    return freemarkerModel.isEmpty(  );
                } catch ( TemplateModelException e )
                {
                    throw new RuntimeException( e );
                }
            }

            @Override
            public boolean containsKey( Object key )
            {
                return get( key ) != null;
            }

            @Override
            public boolean containsValue( Object value )
            {
                throw new UnsupportedOperationException(  );
            }

            @Override
            public Object get( Object key )
            {
                try
                {
                    return freemarkerModel.get( ( String ) key );
                } catch ( TemplateModelException e )
                {
                    throw new RuntimeException( e );
                }
            }

            @Override
            public Object put( String key, Object value )
            {
                throw new UnsupportedOperationException(  );
            }

            @Override
            public Object remove( Object key )
            {
                throw new UnsupportedOperationException(  );
            }

            @Override
            public void putAll( Map<? extends String, ? extends Object> m )
            {
                throw new UnsupportedOperationException(  );
            }

            @Override
            public void clear( )
            {
                throw new UnsupportedOperationException(  );
            }

            @Override
            public Set<String> keySet( )
            {
                throw new UnsupportedOperationException(  );
            }

            @Override
            public Collection<Object> values( )
            {
                throw new UnsupportedOperationException(  );
            }

            @Override
            public Set<java.util.Map.Entry<String, Object>> entrySet( )
            {
                try
                {
                    @SuppressWarnings( "unchecked" )
                    Set<String> keys = env.getKnownVariableNames( );
                    Set<java.util.Map.Entry<String, Object>> res = new HashSet<Map.Entry<String,Object>>( keys.size( ) );
                    for ( final String name : keys )
                    {
                        res.add( new Entry<String, Object>( )
                        {
                            
                            @Override
                            public Object setValue( Object value )
                            {
                                throw new UnsupportedOperationException(  );
                            }
                            
                            @Override
                            public Object getValue( )
                            {
                                try
                                {
                                    return env.getVariable( name );
                                } catch ( TemplateModelException e )
                                {
                                    throw new RuntimeException( e );
                                }
                            }
                            
                            @Override
                            public String getKey( )
                            {
                                return name;
                            }
                        } );
                    }
                    return res;
                } catch ( TemplateModelException e )
                {
                    throw new RuntimeException( e );
                }
            }
            
        };
    }

}
