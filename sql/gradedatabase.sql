-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 25, 2017 at 09:19 PM
-- Server version: 5.7.14
-- PHP Version: 5.6.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gradedatabase`
--

-- --------------------------------------------------------

--
-- Table structure for table `modulegrades`
--

CREATE TABLE `modulegrades` (
  `MID` int(2) NOT NULL,
  `STUD_ID` int(2) NOT NULL,
  `ModuleName` varchar(20) NOT NULL,
  `CA_Mark` int(3) NOT NULL,
  `Exam_Mark` int(3) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `modulegrades`
--

INSERT INTO `modulegrades` (`MID`, `STUD_ID`, `ModuleName`, `CA_Mark`, `Exam_Mark`) VALUES
(1, 1, 'Distributed Systems', 55, 55),
(2, 1, 'Game Design', 45, 58),
(3, 2, 'Distributed Systems', 87, 45),
(4, 2, 'Game Design', 45, 73),
(5, 3, 'Distributed Systems', 43, 65),
(6, 3, 'Game Design', 38, 54);

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `STUD_ID` int(2) NOT NULL,
  `FNAME` varchar(20) NOT NULL,
  `SNAME` varchar(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`STUD_ID`, `FNAME`, `SNAME`) VALUES
(1, 'JOE', 'BLOGGS'),
(2, 'MARY', 'GREY'),
(3, 'BOB', 'GREEN');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `modulegrades`
--
ALTER TABLE `modulegrades`
  ADD UNIQUE KEY `TEST` (`MID`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD UNIQUE KEY `TEST` (`STUD_ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `modulegrades`
--
ALTER TABLE `modulegrades`
  MODIFY `MID` int(2) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `STUD_ID` int(2) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
