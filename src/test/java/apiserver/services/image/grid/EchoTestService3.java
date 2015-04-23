package apiserver.services.image.grid;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteCallable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by mnimer on 6/10/14.
 */
@Ignore
public class EchoTestService3 implements Serializable
{
    private Ignite grid = null;

    @Before
    public void startup() throws IgniteException
    {
        grid = Ignition.start(getGridConfiguration());
    }



    @Test
    public void invokeCFTestServiceClassLoader()
    {
        try
        {
            Assert.assertTrue("Requires an external CF Node to be running", grid.cluster().nodes().size() > 1);

            // Get grid-enabled executor service for nodes where attribute 'worker' is defined.
            ExecutorService exec = grid.cluster().forAttribute("ROLE", "coldfusion-worker").ignite().executorService();


            Future<String> future = exec.submit(new IgniteCallable<String>() {
                public String call() {

                    String cfcPath = new File(this.getClass().getClassLoader().getResource("api-test.cfc").getFile()).getAbsolutePath();
                    try
                    {
                        Class clazz = Class.forName("coldfusion.cfc.CFCProxy");
                        Object cfcProxy = clazz.getDeclaredConstructor(String.class, boolean.class).newInstance(cfcPath, false);

                        System.out.println("class created");

                        Method echoMethod = cfcProxy.getClass().getMethod("invoke", String.class, Object[].class);

                        System.out.println("found method");

                        Object[] cfcArgs = new Object[]{ "Hello World"};
                        Object[] invokeArgs = new Object[]{ "echo", cfcArgs };
                        String result = (String)echoMethod.invoke(cfcProxy, "echo", cfcArgs);

                        System.out.println("executeProxy Result:" +result);
                        return result;
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                        return e.getMessage();
                        //throw new RuntimeException(e);
                    }
                }
            });


            String _result = future.get(60000, TimeUnit.SECONDS);
            Assert.assertEquals("echo: Hello World", _result);
        }
        catch(Exception ge){
            ge.printStackTrace();
        }
    }




    private IgniteConfiguration getGridConfiguration() {
        Map<String, String> userAttr = new HashMap<String, String>();
        userAttr.put("ROLE", "image-service");

        IgniteConfiguration gc = new IgniteConfiguration();
        gc.setGridName("ApiServer");
        gc.setPeerClassLoadingEnabled(true);
        //gc.setRestEnabled(false);
        gc.setUserAttributes(userAttr);


        //GridCacheConfiguration gcc = new GridCacheConfiguration();
        //gcc.setCacheMode(GridCacheMode.PARTITIONED);
        //gcc.setName("documentcache");
        //gcc.setSwapEnabled(true);
        //gcc.setAtomicityMode(GridCacheAtomicityMode.ATOMIC);
        //gcc.setQueryIndexEnabled(true);
        //gcc.setBackups(0);
        //gcc.setStartSize(200000);

        //gc.setCacheConfiguration(gcc);

        return gc;
    }
}
