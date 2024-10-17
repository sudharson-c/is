#include <stdio.h>
#include <string.h>

void vulnerable_function(char *user_input) {
    printf(user_input);  // Vulnerable to format string attack
}
void secure_function(char *user_input) {
    printf("%s", user_input);  // Secure
}
int main() {
    char user_input[100];
    printf("Enter your name: ");
    fgets(user_input, sizeof(user_input), stdin);
    vulnerable_function(user_input);
    printf("Secured:\n");
    printf("Enter your name: ");
    fgets(user_input, sizeof(user_input), stdin);
    secure_function(user_input);
    return 0;
}
