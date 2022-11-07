package G2T6.G2T6.G2T6.services;

import G2T6.G2T6.G2T6.models.Article;

import G2T6.G2T6.G2T6.repository.ArticleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ArticleService {
    private ArticleRepository articles;

    @Autowired
    public ArticleService(ArticleRepository articles) {
        this.articles = articles;
    }
    public List<Article> listArticles() {
        return articles.findAll();
    }
}
