package apiserver.services.image.grid;

import apiserver.workers.coldfusion.services.TestService;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfiguration;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.lang.GridCallable;
import org.gridgain.grid.lang.GridRunnable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
public class EchoTestService implements Serializable
{
    private Grid grid = null;

    @Before
    public void startup() throws GridException {
        grid = GridGain.start(getGridConfiguration());
    }


    @Test
    public void invokeDynamicCFEcho()
    {
        try
        {
            Assert.assertTrue("Requires an external CF Node to be running", grid.nodes().size() > 1);

            // Get grid-enabled executor service for nodes where attribute 'worker' is defined.
            ExecutorService exec = grid.forAttribute("ROLE", "coldfusion-worker").compute().executorService();

            final String cfcPath = "/Applications/ColdFusion10/cfusion/wwwroot/apiserver-inf/components/v1/api-test.cfc";

            Future<String> future = exec.submit(new GridCallable<String>() {
                @Override public String call() {
                    try
                    {
                        System.out.println("creating cfc:" +cfcPath);

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
                        //throw new RuntimeException(e);
                    }
                    return null;
                }
            });


            String _result = future.get();
            Assert.assertEquals("echo: Hello World", _result);
        }
        catch(Exception ge){
            ge.printStackTrace();
        }
    }




    private GridConfiguration getGridConfiguration() {
        Map<String, String> userAttr = new HashMap<String, String>();
        userAttr.put("ROLE", "image-service");

        GridConfiguration gc = new GridConfiguration();
        gc.setGridName("ApiServer");
        gc.setPeerClassLoadingEnabled(true);
        gc.setRestEnabled(false);
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
