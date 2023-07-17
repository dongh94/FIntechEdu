from flask import Flask, render_template, request

app = Flask(__name__)

@app.route("/")
def index():
    return render_template('index.html')

@app.route("/hello/<user>")
def hello_name(user):
    return render_template('hello.html', name = user)

@app.route("/bmi_input")
def bmi_input():
    return render_template("bmi_input.html")

@app.route("/bmi_output", methods = ["POST", "GET"])
def bmi_output():
    name = request.form.get("name")
    height = int(request.form.get("height"))
    weight = int(request.form.get("weight"))
    bmi = round(weight/ ((height/100) ** 2), 2)
    return render_template("bmi_output.html", name= name, height = height, weight = weight, bmi = bmi)


@app.route("/grade_input")
def grade_input():
    return render_template('grade_input.html')

@app.route("/grade_output", methods = ["POST", "GET"])
def grade_output():
    if request.method == "POST":
        result = request.form
        return render_template("grade_output.html", result = result)
    
@app.route("/tax_input")
def tax_input():
    return render_template("tax_input.html")
    
@app.route("/tax_output", methods=["POST", "GET"])
def tax_output():
    if request.method == "POST":
        income = int(request.form.get("income"))
        dependents = int(request.form.get("dependents"))
        taxrate = 0.2
        standarddeduction = 10000
        adddeduct = 3000
        tax = (income - (standarddeduction + adddeduct * dependents)) * taxrate
        return render_template("tax_output.html", tax = tax)
    
@app.route("/loan_input")
def loan_input():
    return render_template("loan_input.html")

@app.route("/loan_output", methods = ["POST", "GET"])
def loan_output():
    if request.method == "POST":
        investment = int(request.form.get("investment"))
        years = int(request.form.get("years"))
        percent = int(request.form.get("percent")) / 100
        payment = 0
        cost = 0
        p = percent / 12
        n = years * 12
        # 이자 계산
        pr = (investment * p * (( 1 + p ) ** n))
        ch = ( 1 + p ) ** n - 1
        payment = pr // ch
        cost = n * payment
        return render_template("loan_output.html", investment = investment, years = years, percent = percent * 100, payment = payment, cost = cost)
        
@app.route("/simplyme")
def simply_me():
    return render_template("simply_me.html")
    
    
if __name__ == "__main__":
    app.run(debug=True)