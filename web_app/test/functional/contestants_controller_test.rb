require 'test_helper'

class ContestantsControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:contestants)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create contestant" do
    assert_difference('Contestant.count') do
      post :create, :contestant => { }
    end

    assert_redirected_to contestant_path(assigns(:contestant))
  end

  test "should show contestant" do
    get :show, :id => contestants(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => contestants(:one).to_param
    assert_response :success
  end

  test "should update contestant" do
    put :update, :id => contestants(:one).to_param, :contestant => { }
    assert_redirected_to contestant_path(assigns(:contestant))
  end

  test "should destroy contestant" do
    assert_difference('Contestant.count', -1) do
      delete :destroy, :id => contestants(:one).to_param
    end

    assert_redirected_to contestants_path
  end
end
