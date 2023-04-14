package lithium.service.cashier.processor.cc.trustspay.data.enums;

import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
public enum ErrorCode implements Serializable {
	
	I0001("Merchant Number cannot be empty!"),
	I0002("Gateway Number cannot empty!"),
	I0003("Gateway of Merchant Number cannot be empty"),
	I0004("Gateway of Merchant Number is incorrect"),
	I0005("Merchant Number is not actived"),
	I0006("Merchant Number is disabled"),
	I0007("Merchant Number is canceled"),
	I0008("The state of merchant Number is anomalous"),
	I0009("Gateway Number is not actived"),
	I0010("Gateway Number is disabled"),
	I0011("Gateway Number is canceled"),
	I0012("The state of gateway Number is anomalous"),
	I0013("Encryption value is incorrect"),
	I0014("Informal gateway Number accesses to formal interface"),
	I0015("Untested gateway Number accesses to test interface"),
	I0016("The gateway Number does not blind the interface"),
	I0017("Merchant order Number is empty"),
	I0018("Order Number cannot exceed 50 characters"),
	I0019("Order value cannot be empty"),
	I0020("Order value is incorrect"),
	I0021("The decimal place of order value should be between 0-2 digit"),
	I0022("Order currency is empty"),
	I0023("Order currency is incorrect"),
	I0024("Return URL cannot be empty"),
	I0025("The length of returning URL cannot exceed 1000 characters"),
	I0026("Card No cannot be empty"),
	I0027("Please input 13 or 16 digit"),
	I0028("Please input number"),
	I0029("Card Number should begin with 4 or 5"),
	I0030("Card Number is incorrect"),
	I0031("Month cannot be empty"),
	I0032("Please input double digit only;"),
	I0033("Please input numbers only"),
	I0034("The month should be between 1-12"),
	I0035("Year cannot be empty"),
	I0036("Please input 4-digit only"),
	I0037("Please input numbers only"),
	I0038("Year and month cannot be smaller than current date and greater than 10 year"),
	I0039("Verification code cannot be empty"),
	I0040("Please input 3-digit Verification code"),
	I0041("Please input numbers only"),
	I0042("Issuing bank cannot be empty"),
	I0043("Please input between 2-50 characters only"),
	I0044("First name cannot be empty"),
	I0045("Please input between 2-50 characters only"),
	I0046("Last name cannot empty"),
	I0047("Please input between 2-50 characters only"),
	I0048("E-mail address cannot be empty"),
	I0049("Please input between 2-100 characters only"),
	I0050("E-mail address format is incorrect"),
	I0051("The phone number of card holder cannot be empty"),
	I0052("Please input between 2-50 characters only"),
	I0053("The country of cardholder cannot be empty"),
	I0054("Please input between 2-50 characters only"),
	I0055("The address of cardholder cannot be empty"),
	I0056("Please input between 2-100 characters only"),
	I0057("The zip code of cardholder cannot be empty"),
	I0058("Please input between 2-50 characters only"),
	I0059("Property{0},can not be empty"),
	I0060("Property{0},length is more than {1}"),
	I0061("Merchant order NUMBER has unsuccessful transation"),
	I0062("Host index exists"),
	I0063("The state/province of cardholder cannot be empty"),
	I0064("Please input between 2-50 characters only"),
	I0065("The city of cardholder cannot be empty"),
	I0066("Please input no more than 100 characters"),
	I0067("Please input no more than 500 characters"),
	I0068("It did not pass 2-Party limitation. (Server IP not whitelisted)."),
	R0000("High risk"),
	R0001("Do not set up the limitation of amount"),
	C0001("Do not pass the number of times that payment are made limitation"),
	C0002("Merchant gateway Number unbinds the channel"),
	C0003("Merchant gateway Number do not set up deduction rate"),
	C0004("Channel deduction rate of merchant gateway Number is incorrect"),
	C0005("Channel deduction rate of merchant gateway Number is incorrect"),
	C0006("The exchange rate of currency does not set up"),
	C0007("Obtaining currency failed"),
	C0008("Merchant does not bind payment domain name"),
	C0009("Merchant gateway Number do not bind channel {0}"),
	S0001("Merchant gateway Number do not set up deduction rate"),
	S0002("Fail to save to anomalous transation list"),
	S0003("Fail to save to the information list of cardholder"),
	S0004("Fail to save to the informal transation list"),
	S0005("Fail to save to the additional transation list"),
	S0006("Channel information the system obtained is anomalous"),
	S0007("Fail to save transation information"),
	S0008("Fail to update testing list"),
	S0009("Fail to delete anomalous record"),
	S0010("Fail to save transation record"),
	S0011("Fail to obtain domain name the merchant bound"),
	S0012("Fail to save the information list of cardholder"),
	S0013("Fail to obtain the information of bank channel"),
	S0014("System Exception"),
	E0001("Channel does not bind e-mail domain name"),
	E0002("The operation has timed out"),
	E0003("The operation has timed out"),
	E0004("Sending failed"),
	E0005("Invoking failed"),
	E0006("The channel code that the bank returned does not exist"),
	T0001("Success");
	
	ErrorCode(String description) {
		this.description = description;
	}

	@Getter
	@Accessors(fluent = true)
	private String description;	

}


