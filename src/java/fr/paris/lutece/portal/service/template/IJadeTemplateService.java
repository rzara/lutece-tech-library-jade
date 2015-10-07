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

import java.util.Locale;

import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Template service interface for the jade templating engine
 * 
 * @see http://jade-lang.com/
 */
public interface IJadeTemplateService
{
    /**
     * Engine initialization
     * @param strDefaulPath parent path for template lookup
     */
    void init( String strDefaulPath );
    
    /**
     * Determine if the engine can handle the template
     * @param strTemplate the template to check
     * @return <code>true</code> if this engine can handle the template, <code>false</code> otherwise
     */
    boolean canHandle( String strTemplate );

    /**
     * Add a shared variable into every template
     * @param name name of the shared variable
     * @param obj value
     */
    void setSharedVariable( String name, Object obj);
    
    /**
     * Load and process a jade template
     * @param strPath template directory
     * @param strTemplate template path from the template directory
     * @param locale the locale
     * @param model the model 
     * @return the processed template
     */
    HtmlTemplate loadTemplate( String strPath, String strTemplate, Locale locale, Object model );

    /**
     * Process a jade template
     * @param templateData the template
     * @param locale the locale
     * @param model the model 
     * @return the processed template
     */
    HtmlTemplate loadTemplate( String templateData, Locale locale, Object model );
    
    /**
     * Reset the template cache
     */
    void resetCache(  );

}
