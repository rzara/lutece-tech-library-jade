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

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Template service based on the jade templating engine
 * @see http://jade-lang.com/
 */
public abstract class AbstractJadeTemplateService implements IJadeTemplateService
{
    /** Suffix of jade template files */
    private static final String JADE_SUFFIX = ".jade";
    
    private String _strDefaultPath;
    private JadeConfiguration _jadeConfiguration;
    private DelegatingStringTemplateLoader _templateLoader;
   
    /**
     * Get the absolute path from relative path
     * @param strPath the path
     * @return the absolute path from relative path
     */
    protected abstract String getAbsolutePathFromRelativePath( String strPath );

    
    @Override
    public void init( String strDefaultPath )
    {
        if ( strDefaultPath == null )
        {
            _strDefaultPath = "";
        } else
        {
            _strDefaultPath = strDefaultPath;
            if ( !_strDefaultPath.endsWith( "/" ) )
            {
                _strDefaultPath = _strDefaultPath + "/";
            }
        }
        _jadeConfiguration = new JadeConfiguration(  );
        _templateLoader = new DelegatingStringTemplateLoader( new FileTemplateLoader( getAbsolutePathFromRelativePath( _strDefaultPath ), "UTF-8" ) );
        _jadeConfiguration.setTemplateLoader( _templateLoader );
    }
    
    @Override
    public boolean canHandle( String strTemplate )
    {
        return strTemplate != null && strTemplate.endsWith( JADE_SUFFIX );
    }

    @Override
    public void setSharedVariable( String name, Object obj )
    {
        if ( _jadeConfiguration == null)
        {
            throw new IllegalStateException( "init must be called first" );
        }
        _jadeConfiguration.getSharedVariables(  ).put( name, obj );
    }
    
    @Override
    public HtmlTemplate loadTemplate( String strPath, String strTemplate, Locale locale, Object model )
    {
        try
        {
            String strTemplatePath;
            if ( _strDefaultPath.equals( strPath ) )
            {
                strTemplatePath = strTemplate;
            } else
            {
                strTemplatePath = getAbsolutePathFromRelativePath( new File( strPath, strTemplate ).getPath(  ) );
            }
            JadeTemplate template = _jadeConfiguration.getTemplate( strTemplatePath );
            
            Map<String, Object> rootModel = getJadeModelFromFreemarkerModel( model );

            return new HtmlTemplate( _jadeConfiguration.renderTemplate( template, rootModel ) );

        } catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }


    @SuppressWarnings( { "unchecked", "rawtypes" } )
    private Map<String, Object> getJadeModelFromFreemarkerModel( Object model )
    {
        Map<String, Object> rootModel;
        if ( model instanceof Map<?, ?> )
        {
            rootModel = ( Map<String, Object> ) model;
        } else {
            rootModel = ( Map ) new BeanMap( model );
        }
        return rootModel;
    }
    
    @Override
    public HtmlTemplate loadTemplate( String templateData, Locale locale, Object model )
    {
        try
        {
            _templateLoader.setStringTemplate( templateData );
            JadeTemplate template = _jadeConfiguration.getTemplate( DelegatingStringTemplateLoader.TEMPLATE_NAME );
            Map<String, Object> rootModel = getJadeModelFromFreemarkerModel( model );

            return new HtmlTemplate( _jadeConfiguration.renderTemplate( template, rootModel ) );

        } catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
    
    @Override
    public void resetCache(  )
    {
        _jadeConfiguration.clearCache(  );
    }

}
