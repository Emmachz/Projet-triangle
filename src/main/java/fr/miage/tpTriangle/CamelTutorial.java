package fr.miage.tpTriangle;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import java.util.List;
import java.util.Map;

public class CamelTutorial extends RouteBuilder {
    //@ConfigProperty(name = "quarkus.artemis.username")
    String userName = "Turra";

    @Override
    public void configure() throws Exception {
        from("file:data/triangle").unmarshal().csv().process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        //System.out.println(exchange.getMessage().getBody());
                        List<List<String>> data = (List<List<String>>) exchange.getIn().getBody();
                        List<String> point1 = List.of(data.get(0).get(0).split(";"));
                        List<String> point2 = List.of(data.get(1).get(0).split(";"));
                        List<String> point3 = List.of(data.get(2).get(0).split(";"));
                        //System.out.println(point1);
                        Point p1 = new Point(Double.parseDouble(point1.get(0)),
                                Double.parseDouble(point1.get(1)));

                        Point p2 = new Point(Double.parseDouble(point2.get(0)),
                                Double.parseDouble(point2.get(1)));

                        Point p3 = new Point(Double.parseDouble(point3.get(0)),
                                Double.parseDouble(point3.get(1)));

                        Triangle triangle = new Triangle(p1, p2, p3);

                        if (triangle.isEquilateralTriangle()) {
                            System.out.println("le triangle est equilatéral");
                            exchange.getMessage().setHeader("type", TypeTriangle.EQUILATERAL.name());
                        } else {
                            System.out.println("le triangle n'est pas equilatéral");
                            exchange.getMessage().setHeader("type", TypeTriangle.OTHER.name());
                        }
                        exchange.getMessage().setBody(triangle);


                    }
                }).choice().when(header("type").isEqualTo(TypeTriangle.EQUILATERAL.name())).marshal().json()
                .to("sjms2:M1.equilateral-" + this.userName)
                .when(header("type").isEqualTo(TypeTriangle.OTHER.name()))
                .marshal().jacksonXml()
                .to("sjms2:M1.autre-" + this.userName);
        ;
        from("sjms2:M1.equilateral-" + this.userName).unmarshal().json().process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                double perimetre = 0;
                System.out.println(exchange.getMessage().getBody());
                Map<String, Map<String, Double>> list = (Map<String, Map<String, Double>>) exchange.getMessage().getBody();
                Point a = new Point(list.get("pt1").get("x"),list.get("pt1").get("y"));
                Point b = new Point(list.get("pt2").get("x"),list.get("pt2").get("y"));
                Point c = new Point(list.get("pt3").get("x"),list.get("pt3").get("y"));

                Triangle triangle = new Triangle(a, b, c);

                exchange.getMessage().reset();
                perimetre= triangle.calculatePerimeterEquilateral();
                exchange. getMessage().setBody(perimetre);
            }
        }).marshal().json().to("file:data/perimeter");

        from("sjms2:M1.autre-" + this.userName).unmarshal().jacksonXml().process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                double perimetre = 0;
                Map<String, Map<String, String>> list = (Map<String, Map<String, String>>) exchange.getMessage().getBody();

                Point a = new Point(Double.parseDouble( list.get("a").get("x"))
                        ,Double.parseDouble(list.get("a").get("y")));
                Point b = new Point(Double.parseDouble(list.get("b").get("x"))
                        ,Double.parseDouble(list.get("b").get("y")));
                Point c = new Point(Double.parseDouble(list.get("c").get("x"))
                        ,Double.parseDouble(list.get("c").get("y")));
                Triangle triangle = new Triangle(a,b,c);

                exchange.getMessage().reset();
                perimetre= triangle.calculatePerimeter();
                exchange. getMessage().setBody(perimetre);
            }
        }).marshal().json().to("file:data/perimeter");
    }

}
