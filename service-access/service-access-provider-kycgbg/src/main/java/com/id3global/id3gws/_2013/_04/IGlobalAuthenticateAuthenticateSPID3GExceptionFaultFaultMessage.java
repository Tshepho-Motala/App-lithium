
package com.id3global.id3gws._2013._04;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.3.3
 * 2019-10-10T16:55:58.955+02:00
 * Generated source version: 3.3.3
 */

@WebFault(name = "ID3gException", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
public class IGlobalAuthenticateAuthenticateSPID3GExceptionFaultFaultMessage extends Exception {

    private com.id3global.id3gws._2013._04.ID3GExceptionType id3GException;

    public IGlobalAuthenticateAuthenticateSPID3GExceptionFaultFaultMessage() {
        super();
    }

    public IGlobalAuthenticateAuthenticateSPID3GExceptionFaultFaultMessage(String message) {
        super(message);
    }

    public IGlobalAuthenticateAuthenticateSPID3GExceptionFaultFaultMessage(String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    public IGlobalAuthenticateAuthenticateSPID3GExceptionFaultFaultMessage(String message, com.id3global.id3gws._2013._04.ID3GExceptionType id3GException) {
        super(message);
        this.id3GException = id3GException;
    }

    public IGlobalAuthenticateAuthenticateSPID3GExceptionFaultFaultMessage(String message, com.id3global.id3gws._2013._04.ID3GExceptionType id3GException, java.lang.Throwable cause) {
        super(message, cause);
        this.id3GException = id3GException;
    }

    public com.id3global.id3gws._2013._04.ID3GExceptionType getFaultInfo() {
        return this.id3GException;
    }
}
