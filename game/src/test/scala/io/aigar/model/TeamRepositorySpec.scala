package io.aigar.model

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._
import org.scalatest._

class TeamRepositorySpec extends FlatSpec with Matchers with BeforeAndAfterEach {

  private var teamRepository:TeamRepository = null
  private var team1: Team = null
  private var team2: Team = null
  private var team3: Team = null

  override def beforeEach: Unit = {
    val cpds = new ComboPooledDataSource
    cpds.setDriverClass("org.h2.Driver")
    cpds.setJdbcUrl("jdbc:h2:mem:test")
    cpds.setUser("root")
    cpds.setPassword("")
    cpds.setMinPoolSize(1)
    cpds.setAcquireIncrement(1)
    cpds.setMaxPoolSize(50)

    val db = Database.forDataSource(cpds)
    teamRepository = new TeamRepository(db)

    team1 = teamRepository.createTeam(Team(None, "EdgQWhJ!v&", "team1", 0))
    team2 = teamRepository.createTeam(Team(None, "not_that_secret", "team2", 50))
    team3 = teamRepository.createTeam(Team(None, "xx3ddfas3", "team3", 56))
    super.beforeEach()
  }

  override def afterEach: Unit = {
    try super.afterEach()
    teamRepository.dropSchema()
  }

  it should "create a new team object and return it" in {
    teamRepository.createTeam(Team(None, "EdgQWhJ!v&", "team4", 0)) should equal(Team(Some(4), "EdgQWhJ!v&", "team4", 0))
  }

  it should "read the team 2 by its id and equal it" in {
    teamRepository.readTeam(team2.id.get).get should equal(team2)
  }

  it should "read a non-existing id and return nothing" in {
    teamRepository.readTeam(258741) shouldBe None
  }
  /*

  def test4 = teamRepository.updateTeam(1) must_=== true
  def test5 = teamRepository.updateTeam(3) must_=== false

  def test6 = teamRepository.deleteTeam(team3.id.get) must_=== true
  def test7 = teamRepository.deleteTeam(585858) must_=== false

  def test8 =  teamRepository.getTeams() must_=== List(team1, team2)
  */

}
