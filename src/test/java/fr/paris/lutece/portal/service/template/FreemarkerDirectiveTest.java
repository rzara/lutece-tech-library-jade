package fr.paris.lutece.portal.service.template;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import fr.paris.lutece.util.html.HtmlTemplate;

public class FreemarkerDirectiveTest
{

    @Test
    public void test( ) throws URISyntaxException, IOException
    {
        final String testTemplate = "freemarkerTemplate.html";
        Map<String, String> model = new HashMap<String, String>();
        model.put( "variable", "value" );
        assertTemplateResult(testTemplate, model );
    }
    
    @Test
    public void testInclude( ) throws URISyntaxException, IOException
    {
        final String testTemplate = "freemarkerTemplate_include.html";
        Map<String, String> model = new HashMap<String, String>();
        model.put( "variable", "value" );
        assertTemplateResult(testTemplate, model );
    }

    private void assertTemplateResult( String testTemplate, Object model  ) throws IOException, URISyntaxException
    {
        IFreeMarkerTemplateService freemarkerTemplaceService = new AbstractFreeMarkerTemplateService( )
        {
            
            @Override
            public String getDefaultPattern( Locale arg0 )
            {
                return "dd/MM/YYYY";
            }
            
            @Override
            public String getAbsolutePathFromRelativePath( String arg0 )
            {
                return arg0;
            }
        };
        String templateDir = Paths.get( getClass( ).getResource( "/freemarkerTemplate.html" ).toURI( ) ).getParent( ).toString( );
        freemarkerTemplaceService.init( templateDir );
        IJadeTemplateService jadeTemplateService = new AbstractJadeTemplateService( )
        {
            
            @Override
            public String getAbsolutePathFromRelativePath( String strPath )
            {
                // TODO Auto-generated method stub
                return strPath;
            }
        };
        jadeTemplateService.init( templateDir );
        freemarkerTemplaceService.setSharedVariable( "jade", new FreemarkerDirective( jadeTemplateService ) );
        HtmlTemplate template = freemarkerTemplaceService.loadTemplate( templateDir, testTemplate, Locale.FRENCH, model );
        try (Scanner scan = new Scanner(getClass( ).getResourceAsStream("/result/" + testTemplate), "UTF-8") ) {
            String expected = scan.useDelimiter("\\A").next();
            Assert.assertEquals( expected, template.getHtml( ) );
        }
    }

}
