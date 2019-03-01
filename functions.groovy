#!/usr/bin/env groovy

def upload_package_command(version, conanfile_path, conan_user, conan_channel) {
  def builds
  String buildCmd = "conan upload ${conanfile_path}@${conan_user}/${conan_channel} -r ${conan_user}/${conan_channel}"

  builds = """
    ${buildCmd}
  """
  return builds
}

def conanfile_path(jenkinsfile_path, version) {
  def regexSuffix = ~/\/Jenkinsfile$/
  def path = "${jenkinsfile_path}" - regexSuffix
  def conanfile = "${path}/${version}"
  return conanfile
}

def create_external_build_commands(version, profiles, target_oss, target_architectures, build_types, conanfile_path, conan_user, conan_channel) {
  // clean the input parameters
  profiles = "${profiles}".replaceAll("\\s", "").split(',')
  target_oss = "${target_oss}".replaceAll("\\s", "").split(',')
  target_architectures = "${target_architectures}".replaceAll("\\s", "").split(',')
  build_types = "${build_types}".replaceAll("\\s", "").split(',')

  // NEED TO PASS the package name to this parameter pkg_name
  pkg_name =  conanfile_path - 'tools/'
  regexTools = ~/\btools\w*\b\//
  println "----TESTING ----"
  println pkg_name
  println regexTools
  test_pkg_name = "${conanfile_path}" - regexTools
  println test_pkg_name
  println "---- TESTING ----"


  // Loop to create all build tasks
  def builds = [:]
  for (prof in profiles) {
    for (t_os in target_oss) {
      for (t_arch in target_architectures) {
        for (b_type in build_types) {
          String buildName = "${prof}-${b_type}-${t_os}"
          String buildCmd = "conan create ${conanfile_path} -pr ${prof} ${pkg_name}@${conan_user}/${conan_channel}"

          if (b_type.length() > 0) {
            buildCmd += " -s build_type=${b_type}"
          }
          if (t_os.length() > 0) {
            buildCmd += " -s os=${t_os}"
          }
          if (t_arch.length() > 0) {
            buildCmd += " -s arch=${t_arch}"
          }
          builds[buildName] = """
            ${buildCmd}
          """
        }
      }
    }
  }
  return builds
}

def create_dependencies_build_commands(version, profiles, target_oss, target_architectures, build_types, conanfile_path, conan_user, conan_channel) {
  // clean the input parameters
  profiles = "${profiles}".replaceAll("\\s", "").split(',')
  target_oss = "${target_oss}".replaceAll("\\s", "").split(',')
  target_architectures = "${target_architectures}".replaceAll("\\s", "").split(',')
  build_types = "${build_types}".replaceAll("\\s", "").split(',')

  // Loop to create all build tasks
  def builds = [:]

  for (prof in profiles) {
    for (t_os in target_oss) {
      for (t_arch in target_architectures) {
        for (b_type in build_types) {
          String buildName = "${prof}-${b_type}-${t_os}"
          String buildCmd = "conan create ${conanfile_path} -pr ${prof} ${conan_user}/${conan_channel}"

          if (b_type.length() > 0) {
            buildCmd += " -s build_type=${b_type}"
          }
          if (t_os.length() > 0) {
            buildCmd += " -s os=${t_os}"
          }
          if (t_arch.length() > 0) {
            buildCmd += " -s arch=${t_arch}"
          }
          builds[buildName] = """
            ${buildCmd}
          """
        }
      }
    }
  }
  return builds
}

return this;
