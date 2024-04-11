package board;

import board.models.domain.ResultKt;
import board.models.dto.ResultDTO;
import board.exceptions.*;
import board.models.domain.Game;
import board.models.domain.Result;
import board.models.domain.TeamScore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryBoardButJava implements Board {

    private final List<Game> board = new ArrayList<>();

    @NotNull
    @Override
    public String addGame(String home, String away)
            throws InvalidGameException, TeamAlreadyPlayingException {
        if (home == null || away == null) {
            throw new IllegalArgumentException();
        } else if (home.equals(away)) {
            throw new InvalidGameException(String.format("Team %s cannot play itself", home));
        } else if (isTeamAlreadyPlaying(home)) {
            throw new TeamAlreadyPlayingException(
                    String.format("Host team %s is already playing a game. End previous game before adding.", home)
            );
        } else if (isTeamAlreadyPlaying(away)) {
            throw new TeamAlreadyPlayingException(
                    String.format("Away team %s is already playing a game. End previous game before adding.", away)
            );
        } else {
            String gameID = generateGameId(home, away);
            board.add(
                    new Game(
                            new Result(
                                    new TeamScore(
                                            new AtomicInteger(0),
                                            home
                                    ),
                                    new TeamScore(
                                            new AtomicInteger(0),
                                            away
                                    )
                            ),
                            gameID,
                            System.currentTimeMillis()

                    )
            );

            return gameID;
        }
    }

    private boolean isTeamAlreadyPlaying(String team) {
        return board.stream().anyMatch(
                game -> (
                        game.getResult().getHome().getTeam().equals(team)
                                || game.getResult().getAway().getTeam().equals(team)
                )
        );
    }

    @NotNull
    @Override
    public ResultDTO removeGame(String gameId) throws UnknownGameException {
        if (gameId == null) {
            throw new IllegalArgumentException("Game ID cannot be null");
        } else if (board.stream().anyMatch(game -> !game.getId().equals(gameId))) {
            throw new UnknownGameException(
                    String.format("Score board does not contain the game matching the predicate: GAME ID: %s. Please, try to identify the game by home and away teams.", gameId)
            );
        } else {
            Game gameToRemove = board
                    .stream()
                    .filter(game -> game.getId().equals(gameId))
                    .findFirst()
                    .orElse(null);
            if (gameToRemove != null) {
                board.remove(gameToRemove);
                return ResultKt.toDTO(gameToRemove.getResult());
            } else {
                throw new UnknownGameException(
                        String.format("Score board does not contain the game matching the predicate: GAME ID: %s. Please, try to identify the game by home and away teams.", gameId)
                );
            }
        }
    }

    @NotNull
    @Override
    public ResultDTO removeGame(String home, String away) throws UnknownTeamException {
        if (home == null || away == null) {
            throw new IllegalArgumentException("Teams cannot be null");
        } else if (board.stream().noneMatch(
                game -> game.getResult().getHome().getTeam().equals(home) || game.getResult().getAway().getTeam().equals(away))) {
            throw new UnknownTeamException(
                    String.format("Score board does not contain the game matching the predicate: HOME: %s AWAY: %s Please, try to identify the game by game id.", home, away)
            );
        } else {
            Game gameToRemove = board
                    .stream()
                    .filter(game -> game.getResult().getHome().getTeam().equals(home) && game.getResult().getAway().getTeam().equals(away))
                    .findFirst()
                    .orElse(null);
            if (gameToRemove != null) {
                board.remove(gameToRemove);
                return ResultKt.toDTO(gameToRemove.getResult());
            } else {
                throw new UnknownTeamException(
                        String.format("Score board does not contain the game matching the predicate: HOME: %s AWAY: %s Please, try to identify the game by game id.", home, away)
                );
            }
        }
    }

    @NotNull
    @Override
    public String getGameIdByTeam(String team) throws UnknownTeamException {
        if (team == null) {
            throw new IllegalArgumentException("Team value cannot be null");
        } else if (board.stream().noneMatch(
                game -> (game.getResult().getHome().getTeam().equals(team) || game.getResult().getAway().getTeam().equals(team)))
        ) {
            throw new UnknownTeamException(
                    String.format("Score board does not contain the game matching the predicate: TEAM: %s", team)
            );
        } else {
            Game gameWithId = board
                    .stream()
                    .filter(
                            game -> game.getResult().getHome().getTeam().equals(team) || game.getResult().getAway().getTeam().equals(team)
                    ).findFirst()
                    .orElse(null);
            if (gameWithId != null) {
                return gameWithId.getId();
            } else {
                throw new UnknownTeamException(
                        String.format("Score board does not contain the game matching the predicate: TEAM: %s", team)
                );
            }
        }
    }

    @NotNull
    @Override
    public List<ResultDTO> getScoreBoard() {
        return board.stream().map(game -> ResultKt.toDTO(game.getResult())).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<ResultDTO> getScoreBoardDescending() {
        Comparator<Game> comparator = Comparator
                .comparing(
                        game -> (game.getResult().getAway().getScore().get() + game.getResult().getHome().getScore().get())
                );
        comparator = comparator.reversed();
        comparator = comparator.thenComparing(Game::getStartTime);

        return board
                .stream()
                .sorted(comparator)
                .map(game -> ResultKt.toDTO(game.getResult()))
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public ResultDTO updateScore(String gameId, int homeScore, int awayScore) throws InvalidScoreException, UnknownGameException {
        if (gameId == null) {
            throw new IllegalArgumentException("GameId cannot be null or empty");
        } else if (homeScore < 0 || awayScore < 0) {
            throw new InvalidScoreException(
                    String.format("Scores cannot be negative number. Home Score: %s Away Score: %s", homeScore, awayScore)
            );
        } else {
            Game gameWithId = board
                    .stream()
                    .filter(game -> game.getId().equals(gameId))
                    .findFirst()
                    .orElse(null);

            if (gameWithId != null) {
                gameWithId.getResult().getHome().getScore().set(homeScore);
                gameWithId.getResult().getAway().getScore().set(awayScore);
                return ResultKt.toDTO(gameWithId.getResult());
            } else {
                throw new UnknownGameException(
                        String.format("Score board does not contain the game matching the predicate: GAME ID: %s", gameId)
                );
            }
        }
    }

    @Override
    public void goal(String team) throws UnknownTeamException {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null");
        } else {
            Game gameToScoreHome = board
                    .stream()
                    .filter(game -> game.getResult().getHome().getTeam().equals(team))
                    .findFirst()
                    .orElse(null);

            Game gameToScoreAway = board
                    .stream()
                    .filter(game -> game.getResult().getAway().getTeam().equals(team))
                    .findFirst()
                    .orElse(null);

            if (gameToScoreHome != null) {
                gameToScoreHome.getResult().getHome().getScore().incrementAndGet();
            } else if (gameToScoreAway != null) {
                gameToScoreAway.getResult().getAway().getScore().incrementAndGet();
            } else {
                throw new UnknownTeamException(
                        String.format("Score board does not contain the game matching the predicate: TEAM: %s", team)
                );
            }
        }
    }

    @Override
    public void revokeGoal(String team) throws UnknownTeamException, InvalidScoreException {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null");
        } else {
            Game gameToScoreHome = board
                    .stream()
                    .filter(game -> game.getResult().getHome().getTeam().equals(team))
                    .findFirst()
                    .orElse(null);

            Game gameToScoreAway = board
                    .stream()
                    .filter(game -> game.getResult().getAway().getTeam().equals(team))
                    .findFirst()
                    .orElse(null);

            if (gameToScoreHome != null) {
                revokeGoalForAtomicScore(gameToScoreHome.getResult().getHome().getScore(), team);
            } else if (gameToScoreAway != null) {
                revokeGoalForAtomicScore(gameToScoreAway.getResult().getAway().getScore(), team);
            } else {
                throw new UnknownTeamException(
                        String.format("Score board does not contain the game matching the predicate: TEAM: %s", team)
                );
            }
        }
    }

    private void revokeGoalForAtomicScore(AtomicInteger score, String team) throws InvalidScoreException {
        int previous = score.get();

        if (previous > 0) {
            if (!score.compareAndSet(previous, previous - 1)) {
                throw new InvalidScoreException(String.format("The score was changed during operation. Please retry for team: %s", team));
            }
        } else {
            throw new InvalidScoreException(String.format("There is no goal to revoke for the given team: %s", team));
        }
    }

    @NotNull
    @Override
    public ResultDTO getCurrentResult(String gameId) throws UnknownGameException {
        if (gameId == null) {
            throw new IllegalArgumentException("GameId cannot be null or empty");
        } else {
            Game gameWithId = board
                    .stream()
                    .filter(game -> game.getId().equals(gameId))
                    .findFirst()
                    .orElse(null);

            if (gameWithId != null) {
                return ResultKt.toDTO(gameWithId.getResult());
            } else {
                throw new UnknownGameException(
                        String.format("Score board does not contain the game matching the predicate: GAME ID: %s", gameId)
                );
            }
        }
    }

    public void clear() {
        board.clear();
    }

    private String generateGameId(String home, String away) {
        return String.format("%s-%s", home, away);
    }
}
