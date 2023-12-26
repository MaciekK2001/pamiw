DO
$$
BEGIN
INSERT INTO teams (team_id, team_name, league_name, wins, draws, loses, goals_scored, goals_lost) VALUES
(1001, 'FC Barcelona', 'LaLiga', 1, 0, 1, 3, 5),
(1002, 'Real Madrid', 'LaLiga', 1, 1, 0, 6, 2),
(1003, 'Osasuna', 'LaLiga', 0, 2, 1, 6, 8),
(1004, 'Atletico', 'LaLiga', 0, 1, 0, 3, 3);

INSERT INTO results (result_id, team_home_name, team_guest_name, team_home_goals, team_guest_goals, team_home_team_id, team_guest_team_id) VALUES
(1001, 'FC Barcelona', 'Real Madrid', 0, 4, 1001, 1002),
(1002, 'Real Madrid', 'Osasuna', 2, 2, 1002, 1003),
(1003, 'Osasuna', 'FC Barcelona', 1, 3, 1003 ,1001),
(1004, 'Atletico', 'Osasuna', 3, 3, 1004, 1003);

END
$$;