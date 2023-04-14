package com.id3global.id3gws._2013._04;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.3.3
 * 2019-10-10T16:55:59.146+02:00
 * Generated source version: 3.3.3
 *
 */
@WebService(targetNamespace = "http://www.id3global.com/ID3gWS/2013/04", name = "IGlobalOrganisation")
@XmlSeeAlso({com.microsoft.schemas._2003._10.serialization.ObjectFactory.class, ObjectFactory.class, com.microsoft.schemas._2003._10.serialization.arrays.ObjectFactory.class, org.datacontract.schemas._2004._07.globalcheck.ObjectFactory.class, org.datacontract.schemas._2004._07.globalcheck_useraccountlib.ObjectFactory.class})
public interface IGlobalOrganisation {

    @WebMethod(operationName = "GetOrganisations", action = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/GetOrganisations")
    @Action(input = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/GetOrganisations", output = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/GetOrganisationsResponse", fault = {@FaultAction(className = IGlobalOrganisationGetOrganisationsID3GExceptionFaultFaultMessage.class, value = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/GetOrganisationsID3gExceptionFault")})
    @RequestWrapper(localName = "GetOrganisations", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04", className = "com.id3global.id3gws._2013._04.GetOrganisationsElement")
    @ResponseWrapper(localName = "GetOrganisationsResponse", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04", className = "com.id3global.id3gws._2013._04.GetOrganisationsResponseElement")
    @WebResult(name = "GetOrganisationsResult", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
    public com.id3global.id3gws._2013._04.GlobalOrganisationsType getOrganisations(

        @WebParam(name = "ParentOrgID", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.String parentOrgID,
        @WebParam(name = "Page", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.Long page,
        @WebParam(name = "PageSize", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.Long pageSize,
        @WebParam(name = "Search", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.String search,
        @WebParam(name = "Filter", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.Long filter
    ) throws IGlobalOrganisationGetOrganisationsID3GExceptionFaultFaultMessage;

    @WebMethod(operationName = "UpdateOrganisationDetails", action = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/UpdateOrganisationDetails")
    @Action(input = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/UpdateOrganisationDetails", output = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/UpdateOrganisationDetailsResponse", fault = {@FaultAction(className = IGlobalOrganisationUpdateOrganisationDetailsID3GExceptionFaultFaultMessage.class, value = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/UpdateOrganisationDetailsID3gExceptionFault")})
    @RequestWrapper(localName = "UpdateOrganisationDetails", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04", className = "com.id3global.id3gws._2013._04.UpdateOrganisationDetailsElement")
    @ResponseWrapper(localName = "UpdateOrganisationDetailsResponse", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04", className = "com.id3global.id3gws._2013._04.UpdateOrganisationDetailsResponseElement")
    public void updateOrganisationDetails(

        @WebParam(name = "OrganisationDetails", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        com.id3global.id3gws._2013._04.GlobalOrganisationDetailsType organisationDetails
    ) throws IGlobalOrganisationUpdateOrganisationDetailsID3GExceptionFaultFaultMessage;

    @WebMethod(operationName = "CreateOrganisation", action = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/CreateOrganisation")
    @Action(input = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/CreateOrganisation", output = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/CreateOrganisationResponse", fault = {@FaultAction(className = IGlobalOrganisationCreateOrganisationID3GExceptionFaultFaultMessage.class, value = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/CreateOrganisationID3gExceptionFault")})
    @RequestWrapper(localName = "CreateOrganisation", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04", className = "com.id3global.id3gws._2013._04.CreateOrganisationElement")
    @ResponseWrapper(localName = "CreateOrganisationResponse", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04", className = "com.id3global.id3gws._2013._04.CreateOrganisationResponseElement")
    @WebResult(name = "CreateOrganisationResult", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
    public com.id3global.id3gws._2013._04.GlobalOrganisationType createOrganisation(

        @WebParam(name = "ParentOrgID", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.String parentOrgID,
        @WebParam(name = "OrganisationDetails", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        com.id3global.id3gws._2013._04.GlobalOrganisationDetailsType organisationDetails,
        @WebParam(name = "AdminName", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.String adminName,
        @WebParam(name = "AdminUsername", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.String adminUsername,
        @WebParam(name = "AdminEmail", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.String adminEmail,
        @WebParam(name = "AdminPassword", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.String adminPassword,
        @WebParam(name = "AdminPIN", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.String adminPIN
    ) throws IGlobalOrganisationCreateOrganisationID3GExceptionFaultFaultMessage;

    @WebMethod(operationName = "GetOrganisationDetails", action = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/GetOrganisationDetails")
    @Action(input = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/GetOrganisationDetails", output = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/GetOrganisationDetailsResponse", fault = {@FaultAction(className = IGlobalOrganisationGetOrganisationDetailsID3GExceptionFaultFaultMessage.class, value = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/GetOrganisationDetailsID3gExceptionFault")})
    @RequestWrapper(localName = "GetOrganisationDetails", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04", className = "com.id3global.id3gws._2013._04.GetOrganisationDetailsElement")
    @ResponseWrapper(localName = "GetOrganisationDetailsResponse", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04", className = "com.id3global.id3gws._2013._04.GetOrganisationDetailsResponseElement")
    @WebResult(name = "GetOrganisationDetailsResult", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
    public com.id3global.id3gws._2013._04.GlobalOrganisationDetailsType getOrganisationDetails(

        @WebParam(name = "OrgID", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.String orgID
    ) throws IGlobalOrganisationGetOrganisationDetailsID3GExceptionFaultFaultMessage;

    @WebMethod(operationName = "GetHome", action = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/GetHome")
    @Action(input = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/GetHome", output = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/GetHomeResponse", fault = {@FaultAction(className = IGlobalOrganisationGetHomeID3GExceptionFaultFaultMessage.class, value = "http://www.id3global.com/ID3gWS/2013/04/IGlobalOrganisation/GetHomeID3gExceptionFault")})
    @RequestWrapper(localName = "GetHome", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04", className = "com.id3global.id3gws._2013._04.GetHomeElement")
    @ResponseWrapper(localName = "GetHomeResponse", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04", className = "com.id3global.id3gws._2013._04.GetHomeResponseElement")
    @WebResult(name = "GetHomeResult", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
    public com.id3global.id3gws._2013._04.GlobalHomeType getHome(

        @WebParam(name = "OrgID", targetNamespace = "http://www.id3global.com/ID3gWS/2013/04")
        java.lang.String orgID
    ) throws IGlobalOrganisationGetHomeID3GExceptionFaultFaultMessage;
}
