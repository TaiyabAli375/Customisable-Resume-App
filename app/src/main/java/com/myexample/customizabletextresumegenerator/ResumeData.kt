package com.myexample.customizabletextresumegenerator

data class ResumeData(
    val address: String,
    val email: String,
    val name: String,
    val phone: String,
    val projects: List<Project>,
    val skills: List<String>,
    val summary: String,
    val twitter: String
)