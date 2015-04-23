package apiserver.services.images.controllers;

import apiserver.services.images.services.grid.GridService;
import apiserver.workers.coldfusion.services.EchoCallable;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by mnimer on 5/5/14.
 */
@Controller
@RestController
public class ImageTestController
{
    @Autowired
    private Ignite grid = null;

    @Autowired private GridService gridService;
    private Integer defaultTimeout = 2;

    @RequestMapping(value = "/api/image/test", method= RequestMethod.GET)
    public ResponseEntity<Map> test()
    {
        String _status = "CHECKING";
        Boolean _ping = false;
        try {
            _ping = grid.cluster().pingNode(UUID.fromString("5d59104a-674b-4cec-88a8-86264d02641b"));

            ExecutorService exec = gridService.getColdFusionExecutor();
            Future<byte[]> future = exec.submit(new EchoCallable("OK"));
            byte[] _result = future.get(defaultTimeout, TimeUnit.SECONDS);
            System.out.println("ImageTestController = " +new String(_result));
            _status = new String(_result);
        }catch (Exception ex){
            _status = ex.getMessage();
        }




        HashMap<String, Object> _java = new HashMap<String, Object>();
        _java.put("status", "ok");
        _java.put("timestamp", new Date().getTime());

        HashMap<String, Object> _cf = new HashMap<String, Object>();
        _cf.put("execute", _status);
        _cf.put("ping", _ping);
        _cf.put("timestamp", new Date().getTime());

        HashMap<String, Object> _test = new HashMap<String, Object>();
        _test.put("java", _java);
        _test.put("cf", _cf);

        return new ResponseEntity<Map>(_test, HttpStatus.OK);
    }
}
