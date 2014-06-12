package apiserver.services.image.grid;

import apiserver.workers.coldfusion.services.TestService;
import org.gridgain.grid.lang.GridCallable;

import java.io.Serializable;

/**
 * Created by mnimer on 6/10/14.
 */
public class EchoCallable implements GridCallable
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
