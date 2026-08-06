package com.gumthala.learningapp.data.repository

import com.gumthala.learningapp.data.local.dao.StudentDao
import com.gumthala.learningapp.data.local.dao.TeacherDao

/** Backs Teacher's "my classes" roster and Admin's full student/teacher management. */
class RosterRepository(
    private val studentDao: StudentDao,
    private val teacherDao: TeacherDao
) {
    fun observeStudentsForClass(classLevel: Int) = studentDao.observeByClass(classLevel)
    fun observeStudentsRegisteredBy(teacherId: String) = studentDao.observeByRegisteredBy(teacherId)
    fun observeAllStudents() = studentDao.observeAll()
    fun observeAllTeachers() = teacherDao.observeAll()
    fun observeTeacher(teacherId: String) = teacherDao.observeById(teacherId)
}
