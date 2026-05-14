# save this as shell.nix
{ pkgs ? import <nixpkgs> {}}:

pkgs.mkShell {
  allowUnfree = true;
  packages = [
    pkgs.openjdk
    pkgs.jdt-language-server
    pkgs.android-studio-tools
    pkgs.android-studio
    pkgs.android-tools
  ];
}
