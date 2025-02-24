package jig.erd;

import jig.erd.domain.diagram.ViewPoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("jig-erd")
public class JigErdEndpoint {

    private final JigErd jigErd;

    public JigErdEndpoint(JigErd jigErd) {
        this.jigErd = jigErd;
    }

    @GetMapping
    @ResponseBody
    String erd() {
        Map<ViewPoint, String> map = jigErd.mermaidTextMap();
        return """
                <html>
                <head>
                    <meta charset="UTF-8"/>
                    <title>JIG ERD</title>
                </head>
                <body>
                <section>%s</section>
                <section>%s</section>
                <section>%s</section>
                </body>
                </html>
                """.formatted(map.get(ViewPoint.俯瞰), map.get(ViewPoint.概要), map.get(ViewPoint.詳細));
    }
}
