package G2T6.G2T6.G2T6;

import G2T6.G2T6.G2T6.misc.*;
import G2T6.G2T6.G2T6.models.*;
import G2T6.G2T6.G2T6.models.orders.OptionOrder;
import G2T6.G2T6.G2T6.models.orders.QuestionOrder;
import G2T6.G2T6.G2T6.models.security.User;
import G2T6.G2T6.G2T6.repository.*;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.apache.bcel.classfile.ConstantValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.json.*;
import java.io.InputStream;

@SpringBootApplication
@EnableScheduling
public class TheGreenInvestorApplication {

	public static void saveORepo(OptionRepository oRepo, List<Option> optionList) {
		for (Option option : optionList) {
			oRepo.saveAndFlush(option);
		}
	}

	public static void saveARepo(ArticleRepository aRepo, List<Article> articleList) {
		for (Article article : articleList) {
			aRepo.saveAndFlush(article);
		}
	}

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(TheGreenInvestorApplication.class, args);
		JdbcTemplate template = ctx.getBean(JdbcTemplate.class);
		// public User(String username, String email, String password, String role) {
		QuestionRepository qRepo = ctx.getBean(QuestionRepository.class);
		OptionRepository oRepo = ctx.getBean(OptionRepository.class);

		ArticleRepository aRepo = ctx.getBean(ArticleRepository.class);

		List<Question> qList = new ArrayList<Question>();
		List<Option> oList = new ArrayList<Option>();

		// Dont think need this for now
		// populate questionOrders
		// QuestionOrderRepository questionOrderRepository =
		// ctx.getBean(QuestionOrderRepository.class);
		// OptionOrderRepository optionOrderRepository =
		// ctx.getBean(OptionOrderRepository.class);

		// for (int i = 0; i < 10; i++) {
		// questionOrderRepository.save(new QuestionOrder());
		// optionOrderRepository.save(new OptionOrder());
		// }

		List<Article> aList = new ArrayList<Article>();

		InputStream articlesInput = null;
		articlesInput = TheGreenInvestorApplication.class
				.getClassLoader().getResourceAsStream("articles.json");

		JSONTokener articleTokener = new JSONTokener(articlesInput);
		JSONObject articleObj = new JSONObject(articleTokener);
		JSONArray jsonArticleArray = (JSONArray) articleObj.get("ArticleList");

		for (int i = 0; i < jsonArticleArray.length(); i++) {
			JSONObject article = jsonArticleArray.getJSONObject(i);
			Article newArticle = new Article(article.getString("Description"), article.getString("URL"));
			aList.add(newArticle);
		}

		InputStream questionAndAnswerInput = null;
		questionAndAnswerInput = TheGreenInvestorApplication.class
				.getClassLoader().getResourceAsStream("questions-and-answer.json");

		JSONTokener questionAndAnswerTokener = new JSONTokener(questionAndAnswerInput);
		JSONObject questionAndAnswerObj = new JSONObject(questionAndAnswerTokener);

		JSONArray jsonQuestionArray = (JSONArray) questionAndAnswerObj.get("QuestionList");

		for (int i = 0; i < jsonQuestionArray.length(); i++) {
			JSONObject question = jsonQuestionArray.getJSONObject(i);
			boolean isOpenEnded = question.getBoolean("isOpenEnded");
			Question temp = new Question(question.getString("Question"), question.getString("Image"), isOpenEnded);
			qList.add(temp);

			JSONArray jsonOptionArray = (JSONArray) question.get("Options");

			for (int j = 0; j < jsonOptionArray.length(); j++) {
				JSONObject option = jsonOptionArray.getJSONObject(j);
				Option newOption = null;
				if (isOpenEnded)
					newOption = new Option(option.getString("Option"), option.getString("Feedback"), qList.get(i));
				else
					newOption = new Option(option.getString("Option"), option.getString("Feedback"), qList.get(i),
							option.getInt("sustainbilityPts"), option.getInt("moralePts"), option.getInt("incomePts"),
							option.getInt("costPts"));

				oList.add(newOption);
			}

			qList.get(i).setOptions(oList);
			qRepo.saveAndFlush(qList.get(i));
			saveORepo(oRepo, oList);
			oList.clear();
		}

		// only save after questions are done for articles
		aList.get(0).setQuestion(qList.get(6));
		aList.get(1).setQuestion(qList.get(7));
		aList.get(2).setQuestion(qList.get(8));
		saveARepo(aRepo, aList);

		// UserRepository userRepo = ctx.getBean(UserRepository.class);
		// CurrentState stateRepo = ctx.getBean(CurrentState.class);
		// GameStats gameStatsRepo = ctx.getBean(GameStats.class);
		//
		// User regularUser = userRepo.save(new User("ckasdasd123", "ck123@gmail.com",
		// "Password1232", "GUEST"));
		//
		// CurrentState cs0101 = new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR,
		// State.completed, regularUser);
		// CurrentState cs0102 = new CurrentState(10, State.completed, regularUser);
		//
		// List<CurrentState> csList01 = new ArrayList<>();
		// csList01.add(cs0101);
		// csList01.add(cs0102);
		//
		// GameStats gs1 = new GameStats(10,10,10, regularUser, cs0101);
		// GameStats gs2 = new GameStats(1,1,1, regularUser, cs0102);
		// List<GameStats> gsList01 = new ArrayList<>();
		// gsList01.add(gs1);
		// gsList01.add(gs2);
		//
		// regularUser.setCurrentState(csList01);
		// regularUser.setGameStats(gsList01);
		// stateRepo.save(cs0101);
		// stateRepo.save(cs0102);
		// gameStatsRepo.save(gs1);
		// gameStatsRepo.save(gs2);
		//
		// User user02 = userRepo.save(new User("fasfajisoaisf",
		// "fasfajisoaisf@gmail.com", "PASSWORD123", "GUEST"));
		//
		// CurrentState cs0201 = new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR,
		// State.completed, user02);
		// CurrentState cs0202 = new CurrentState(10, State.completed, user02);
		//
		// List<CurrentState> csList02 = new ArrayList<>();
		// csList01.add(cs0201);
		// csList02.add(cs0202);
		//
		// GameStats gs3 = new GameStats(10,9,10, user02, cs0201);
		// GameStats gs4 = new GameStats(1,2,1, user02, cs0202);
		// List<GameStats> gsList02 = new ArrayList<>();
		// gsList02.add(gs3);
		// gsList02.add(gs4);
		//
		// user02.setCurrentState(csList02);
		// user02.setGameStats(gsList02);
		// stateRepo.save(cs0201);
		// stateRepo.save(cs0202);
		// gameStatsRepo.save(gs3);
		// gameStatsRepo.save(gs4);
		//
		// User user03 = userRepo.save(new User("asf3fq3", "asf3fq3@gmail.com",
		// "PASSWORD12345", "GUEST"));
		//
		// CurrentState cs0301 = new CurrentState(CONSTANTVARIABLES.DEFAULTYEAR,
		// State.completed, user03);
		//
		// List<CurrentState> csList03 = new ArrayList<>();
		// csList03.add(cs0301);
		//
		// GameStats gs5 = new GameStats(10,9,8, user03, cs0301);
		// List<GameStats> gsList03 = new ArrayList<>();
		// gsList03.add(gs5);
		//
		// user03.setCurrentState(csList03);
		// user03.setGameStats(gsList03);
		// stateRepo.save(cs0301);
		// gameStatsRepo.save(gs5);
		//
		// System.out.println(ck.getCurrentState() + "" + ck.getId());
		// System.out.println(kami.getCurrentState() + "" + kami.getId());

	}

}
