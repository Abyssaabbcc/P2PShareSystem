/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50520
Source Host           : localhost:3306
Source Database       : p2p

Target Server Type    : MYSQL
Target Server Version : 50520
File Encoding         : 65001

Date: 2014-12-16 17:27:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for fileshare
-- ----------------------------
DROP TABLE IF EXISTS `fileshare`;
CREATE TABLE `fileshare` (
  `fileName` varchar(255) NOT NULL,
  `fileSize` bigint(20) NOT NULL,
  `userName` varchar(255) NOT NULL,
  `IPaddress` varchar(255) NOT NULL,
  `userPort` int(11) NOT NULL,
  `filePath` varchar(255) NOT NULL,
  PRIMARY KEY (`filePath`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of fileshare
-- ----------------------------
INSERT INTO `fileshare` VALUES ('test1.txt', '5', 'wk51920', '219.245.100.44', '2001', 'C:\\Users\\wk_51920\\Desktop\\test1.txt');
INSERT INTO `fileshare` VALUES ('[RS][neubt]Rational Rose.torrent', '17387', 'wk51920', '219.245.100.44', '2002', 'E:\\[RS][neubt]Rational Rose.torrent');

-- ----------------------------
-- Table structure for userinfo
-- ----------------------------
DROP TABLE IF EXISTS `userinfo`;
CREATE TABLE `userinfo` (
  `userName` varchar(255) NOT NULL,
  `userPassword` varchar(255) NOT NULL,
  `online` int(11) NOT NULL DEFAULT '0',
  `isTransport` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`userName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of userinfo
-- ----------------------------
INSERT INTO `userinfo` VALUES ('wk51920', 'wk871113', '0', '0');
