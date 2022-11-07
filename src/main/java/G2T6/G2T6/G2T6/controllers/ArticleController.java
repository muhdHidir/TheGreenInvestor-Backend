package G2T6.G2T6.G2T6.controllers;

import G2T6.G2T6.G2T6.models.Article;
import G2T6.G2T6.G2T6.services.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
@RestController
public class ArticleController {
    private ArticleService articleService;
    @Autowired
    public ArticleController(ArticleService articleService){
        this.articleService = articleService;
    }

    @GetMapping("/articles")
    public List<Article> getArticles() {
        return articleService.listArticles();
    }
}
