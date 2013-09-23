-- ---------------------------------------------------------------------------------
-- Copyright (C) 2007 Hx Technologies, Inc.
-- 
-- This program is free software; you can redistribute it and/or
-- modify it under the terms of the GNU General Public License
-- version 2 as published by the Free Software Foundation.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with this program; if not, write to the Free Software
-- Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
--
-- $Id: create_db.sql,v 1.1.1.1 2007/10/18 16:04:55 kpearce Exp $
-- Last Revised By   : $Author: kpearce $
-- Last Checked In   : $Date: 2007/10/18 16:04:55 $
-- Last Version      : $Revision: 1.1.1.1 $
--
-- Original Author   : Rafael Chargel
-- Origin            : Hx Technologies Inc.
--
-- ---------------------------------------------------------------------------------
-- Server version	MySQL 5.0.22-standard
--

-- LAST UPDATE:
-- rchargel 04/20/2007 - Removed brokers table and added scp_service
-- rchargel 08/23/2007 - Added date_accessed as a last time accessed column
-- kpearce  10/11/2007 - Cleanup, etc., for open source release

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `service_class_provider`
--

DROP TABLE IF EXISTS `radiology_scp_service`;
CREATE TABLE `radiology_scp_service` (
	`scp_id` 			int		 		NOT NULL auto_increment,
	`ae_title`			VARCHAR(24)	 	NOT NULL,
	`agent_id`          int             NOT NULL,
	`port`				int				NOT NULL,
	`archive_dir` 		varchar(64)		NOT NULL,
	`tmp_dir`			varchar(64)		NOT NULL,
	`log_file_path`		varchar(64)		NOT NULL,
	`log_level`			varchar(16)		NOT NULL DEFAULT 'INFO',
	`thumbnail_width`	int				NOT NULL DEFAULT 300,
	`thumbnail_height`	int				NOT NULL DEFAULT 300,
	`thumbnail_quality` float			NOT NULL DEFAULT 0.1,
	`store_jpeg_on_move` enum('Y','N') 	NOT NULL DEFAULT 'N',
	PRIMARY KEY (`scp_id`),
	UNIQUE KEY `scp_service_ix1` (`ae_title`),
	UNIQUE KEY `scp_service_ix2` (`port`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `ae_peers`
--

DROP TABLE IF EXISTS `radiology_ae_peers`;
CREATE TABLE `radiology_ae_peers` (
    `ae_id`                 int             NOT NULL auto_increment,
    `scp_id`                int             NOT NULL,
    `local_name`            varchar(32)     NOT NULL,
    `find_ae_title`         varchar(24)     NOT NULL,
    `move_ae_title`         varchar(24)     NOT NULL,
    `store_ae_title`        varchar(24)     NOT NULL,
    `resp_ae_title`         varchar(24)     NOT NULL,
    `host`                  varchar(32)     NOT NULL,
    `port`                  int             NOT NULL,
    `query_model`           enum('STUDYROOT','PATIENTROOT','PATIENTSTUDYONLY'),
    `primary_device_type`   varchar(32),
    PRIMARY KEY (`ae_id`),
    UNIQUE KEY `ae_peers_ix1` (`host`,`port`),
    UNIQUE KEY `ae_peers_ix2` (`find_ae_title`),
    UNIQUE KEY `ae_peers_ix3` (`move_ae_title`),
    UNIQUE KEY `ae_peers_ix4` (`store_ae_title`),
    UNIQUE KEY `ae_peers_ix5` (`resp_ae_title`),
    CONSTRAINT `ae_peers_ix1_ibfk_1` FOREIGN KEY (`scp_id`) 
    REFERENCES `radiology_scp_service` (`scp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `instances`
--

DROP TABLE IF EXISTS `radiology_instances`;
CREATE TABLE `radiology_instances` (
  `instance_id` 				int(32) 		NOT NULL auto_increment,
  `series_id` 					int(32) 		NOT NULL,
  `image_number` 				int(32),
  `sop_class_uid` 				varchar(200),
  `sop_instance_uid` 			varchar(200),
  `image_date` 					datetime,
  `image_file_location` 		varchar(200),
  `matrix_rows` 				int(32),
  `matrix_columns` 				int(32),
  `number_of_frames` 			int(32),
  `frame_time` 					varchar(16),
  `frame_time_vector` 			varchar(200),
  `actual_frame_duration` 		int(16),
  `image_type` 					varchar(50),
  `echo_time` 					varchar(16),
  `echo_train_length` 			int(32),
  `repetition_time` 			varchar(16),
  `inversion_time` 				varchar(16),
  `trigger_time` 				varchar(16),
  `scanning_sequence` 			varchar(64),
  `sequence_variant` 			varchar(64),
  `magnetic_field_strength` 	varchar(16),
  `photometric_interpretation`	varchar(50),
  `bits_allocated` 				int(32),
  `bits_stored` 				int(32),
  `high_bit` 					int(32),
  `pixel_aspect_ratio` 			varchar(34),
  `pixel_spacing` 				varchar(34),
  `imager_pixel_spacing` 		varchar(34),
  `pixel_representation` 		int(32),
  `rescale_intercept` 			varchar(32),
  `rescale_slope` 				varchar(32),
  `gantry_detector_tilt` 		varchar(16),
  `table_height` 				varchar(16),
  `table_traverse` 				varchar(16),
  `whole_body_technique` 		varchar(64),
  `filter_type` 				varchar(16),
  `rotation_direction` 			varchar(16),
  `counts_accumulated` 			int(16),
  `scan_length` 				int(16),
  `scan_velocity` 				varchar(16),
  `spacing_between_slices` 		varchar(16),
  `slice_thickness` 			varchar(16),
  `slice_location` 				varchar(16),
  `image_position_patient` 		varchar(51),
  `image_orientation_patient` 	varchar(102),
  `smallest_image_pixel_value` 	int(16),
  `largest_image_pixel_value` 	int(16),
  `overlay_rows` 				int(32),
  `overlay_columns` 			int(32),
  `overlay_type` 				varchar(50),
  `overlay_origin` 				varchar(50),
  `overlay_bits_allocated` 		int(32),
  `overlay_bit_position` 		int(32),
  `overlay_data` 				varchar(200),
  `window_center` 				varchar(32),
  `window_width` 				varchar(32),
  `pixel_padding` 				varchar(16),
  `presentation_label` 			varchar(100),
  `presentation_description` 	varchar(200),
  `presentation_creation_date` 	datetime,
  `presentation_creator_name` 	varchar(50),
  `thumbnail` 					blob,
  `jpeg_file_location` 			varchar(200),
  `lossless_jpeg_file_location`	varchar(200),
  `date_added` 					datetime,
  `date_updated` 				datetime,
  PRIMARY KEY  (`instance_id`),
  UNIQUE KEY `instances_ix1` (`instance_id`),
  UNIQUE KEY `instances_ix2` (`sop_instance_uid`),
  CONSTRAINT `instances_ibfk_1` FOREIGN KEY (`series_id`) REFERENCES `radiology_series` (`series_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `series`
--

DROP TABLE IF EXISTS `radiology_series`;
CREATE TABLE `radiology_series` (
  `series_id` 							int(32) 		NOT NULL auto_increment,
  `study_id` 							int(32) 		NOT NULL,
  `series_uid` 							varchar(200),
  `series_number` 						int(32),
  `series_date` 						datetime,
  `modality` 							char(4),
  `patient_position` 					varchar(50),
  `patient_orientation`					varchar(50),
  `body_part_examined` 					varchar(50),
  `series_description` 					varchar(100),
  `manufacturer_model_name` 			varchar(200),
  `station_name` 						varchar(32),
  `department_name` 					varchar(64),
  `number_of_series_related_instances` 	int(32),
  `kvp` 								varchar(16),
  `exposure` 							int(32),
  `pulse_sequence_name` 				varchar(16),
  `scan_options` 						varchar(64),
  `laterality` 							varchar(16),
  `protocol_name` 						varchar(64),
  `plate_type` 							varchar(16),
  `phosphor_type` 						varchar(64),
  `cassette_orientation` 				varchar(16),
  `cassette_size` 						varchar(16),
  `sensitivity` 						varchar(16),
  `contrast_bolus_agent` 				varchar(64),
  `frame_of_reference_uid`				varchar(200),
  `last_calibration_date` 				datetime,
  `date_added` 							datetime,
  `date_updated` 						datetime,
  PRIMARY KEY  (`series_id`),
  UNIQUE KEY `series_ix1` (`series_id`),
  UNIQUE KEY `series_ix2` (`series_uid`),
  KEY `series_ix3` (`series_number`),
  KEY `series_ix4` (`modality`),
  KEY `series_ix5` (`frame_of_reference_uid`),
  CONSTRAINT `series_ibfk_1` FOREIGN KEY (`study_id`) REFERENCES `radiology_studies` (`study_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `studies`
--

DROP TABLE IF EXISTS `radiology_studies`;
CREATE TABLE `radiology_studies` (
  `study_id` 							int(32) 		NOT NULL auto_increment,
  `ae_id` 								int(32) 		NOT NULL,
  `study_date` 							datetime,
  `study_uid` 							varchar(200),
  `dicom_study_id` 						varchar(16),
  `accession_num` 						varchar(24),
  `patient_name` 						varchar(50),
  `mrn` 								varchar(40),
  `birth_date` 							date,
  `sex` 								char(1),
  `record_purge_profile_id` 			int(32),
  `study_description` 					varchar(100),
  `referring_physician_name` 			varchar(100),
  `reading_physician_name` 				varchar(100),
  `modalities_in_study` 				int(32),
  `procedure_code_sequence` 			int(32),
  `number_of_study_related_series` 		int(32),
  `number_of_study_related_instances`	int(32),
  `cannot_delete` 						enum('Y','N') 	NOT NULL default 'N',
  `images_cached_date` 					datetime,
  `images_purged_date` 					datetime,
  `images_distributed_date`				datetime,
  `images_purge_score`					int(32),
  `date_added` 							datetime,
  `date_updated` 						datetime,
  `date_accessed`						datetime,
  PRIMARY KEY  (`study_id`),
  UNIQUE KEY `studies_ix1` (`study_id`),
  UNIQUE KEY `studies_ix2` (`study_uid`),
  KEY `studies_ix4` (`study_date`),
  KEY `studies_ix5` (`dicom_study_id`),
  KEY `studies_ix6` (`patient_name`),
  KEY `studies_ix7` (`mrn`),
  CONSTRAINT `studies_ibfk_1` FOREIGN KEY (`ae_id`) REFERENCES `radiology_ae_peers` (`ae_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
