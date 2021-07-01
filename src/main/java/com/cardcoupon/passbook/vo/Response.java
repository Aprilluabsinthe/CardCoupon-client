package com.cardcoupon.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * General Response for VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    /** code for error, if no error, the default code will be 0 */
    private Integer errorCode = 0;
    /** message for error, if no error, the default will be an empty string */
    private String errorMsg = "";
    /** return object, general object */
    private Object data;

    /**
     * Constructor for successful response
     * errorCode = 0, errorMsg = "" as default
     * @param data {@link Object}
     */
    public Response(Object data){
        this.data = data;
    }

    /**
     * succeed response
     * @return successful Response {@link Response}
     */
    public static Response success(){
        return new Response();
    }

    /**
     * fail in response
     * @param errorMsg the String error Message
     * @return failure Response {@link Response}
     */
    public static Response failure(String errorMsg){
        return new Response(-1,errorMsg,null);
    }

}
