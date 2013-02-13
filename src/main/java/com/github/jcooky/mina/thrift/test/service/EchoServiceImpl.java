package com.github.jcooky.mina.thrift.test.service;

import com.github.jcooky.mina.thrift.test.service.gen.EchoService;
import org.apache.thrift.TException;

/**
 * Created with IntelliJ IDEA.
 * User: jcooky
 * Date: 13. 2. 12.
 * Time: 오후 10:17
 * To change this template use File | Settings | File Templates.
 */
public class EchoServiceImpl implements EchoService.Iface {
    @Override
    public String echo(String str) throws TException {
        return "hello";
    }
}
