/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  ukk1
 * Created: Jan 31, 2017
 */

CREATE TABLE IF NOT EXISTS accounts (
id int PRIMARY KEY auto_increment,
username varchar(50),
password varchar(50)
);

CREATE TABLE IF NOT EXISTS data (
text varchar(200)
);