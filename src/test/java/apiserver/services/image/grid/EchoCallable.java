package apiserver.services.image.grid;

import apiserver.workers.coldfusion.services.TestService;
import org.apache.ignite.lang.IgniteCallable;

/**
 * Created by mnimer on 6/10/14.
 */
public class EchoCallable implements IgniteCallable
{
    String msg;

    public EchoCallable(String m) {
        this.msg = m;
    }


    @Override
    public Object call() throws Exception {
        try
        {
            TestService service = new TestService();
            String result = service.execute(this.msg);
            return result;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
        return "ERROR";
    }
}
