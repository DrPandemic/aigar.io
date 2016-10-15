import io.aigar.controller._
import io.aigar.controller.response._
import io.aigar.game._

import org.json4s._
import org.json4s.jackson.JsonMethods._

import org.scalatra.test.specs2._
import org.specs2.matcher._

class ScalatraBootstrapSpec extends WholeAppTest {
  protected implicit val jsonFormats: Formats = DefaultFormats

  sequential
  "Launching the whole application" should {
    "launch a game thread that sets the state of the ranked game" in {
      get("/api/1/game/" + Game.RankedGameId.toString) {
        status must be_==(200).eventually

        val state = parse(body).extract[GameStateResponse].data
        state.id must be_==(Game.RankedGameId)
      }
    }
  }
}
