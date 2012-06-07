package com.v2soft.AndLib.dao;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class Storage {
    public abstract void load(InputStream input) throws Exception;
    public abstract void save(OutputStream output) throws Exception;
}
