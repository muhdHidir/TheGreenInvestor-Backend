package G2T6.G2T6.G2T6.repository;

import G2T6.G2T6.G2T6.models.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    //find by question id
    Article findByQuestionId(Long questionId);
}
