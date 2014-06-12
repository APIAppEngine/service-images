package apiserver.services.image.grid;

import apiserver.workers.coldfusion.services.TestService;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridConfiguration;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.lang.GridCallable;
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
public class EchoTestService2 implements Serializable
{
    private Grid grid = null;

    @Before
    public void startup() throws GridException {
        grid = GridGain.start(getGridConfiguration());
    }



    @Test
    public void invokeCFTestService()
    {
        invokeCFTestService("Invoke CF Test");
    }

    public void invokeCFTestService(String msg)
    {
        final String msg2 = msg;
        try
        {
            Assert.assertTrue("Requires an external CF Node to be running", grid.nodes().size() > 1);

            // Get grid-enabled executor service for nodes where attribute 'worker' is defined.
            ExecutorService exec = grid.forAttribute("ROLE", "coldfusion-worker").compute().executorService();


            Future<String> future = exec.submit(new EchoCallable(msg));
            /***
            Future<String> future = exec.submit(new GridCallable<String>() {
                public String call() {
                    try
                    {
                        TestService service = new TestService();
                        String result = service.execute(msg2);
                        return result;
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                        //throw new RuntimeException(e);
                    }
                    return "ERROR";
                }
            });
            **/

            String _result = future.get(60000, TimeUnit.SECONDS);
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
