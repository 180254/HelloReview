/**
 * Fix, server should server 400 instead of 500, if parameter was not added to request.
 * <p>
 * If such statement was used:
 *
 * @RequestParam("xx") long id
 * and such parameter doesn't exist,
 * server will throw code 500.
 * <p>
 * If own type is used:
 * @RequestParam("xx") long id
 * and such parameter doesn't exist,
 * server will throw 'better; code 400.
 * <p>
 * Problem:
 * http://stackoverflow.com/questions/27851775/spring-mvc-data-binding-primitive-types
 * http://stackoverflow.com/questions/23679564/spring-mvc-web-application-no-default-constructor-found
 * http://stackoverflow.com/questions/27851775/spring-mvc-data-binding-primitive-types
 * <p>
 * Solution:
 * http://stackoverflow.com/questions/15005438/requestparam-custom-object
 * http://stackoverflow.com/questions/11273443/how-to-configure-spring-conversionservice-with-java-config
 */
package pl.p.lodz.iis.hr.configuration.long2;
